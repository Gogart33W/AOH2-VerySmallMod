$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Шукаємо GameRoot
$possibleRoots = @(
    (Resolve-Path "$ScriptDir\..\..").Path,
    "D:\Games\steamapps\common\Age of History 2 Definitive Edition",
    "C:\Program Files (x86)\Steam\steamapps\common\Age of History 2 Definitive Edition",
    "C:\Program Files\Steam\steamapps\common\Age of History 2 Definitive Edition"
)

$GameRoot = $null
foreach ($root in $possibleRoots) {
    if ($root -and (Test-Path (Join-Path $root "game.jar"))) {
        $GameRoot = $root
        break
    }
}

if (-not $GameRoot) {
    Write-Error "Не знайдено папку зі встановленою грою Age of History 2!"
    exit 1
}

Write-Host "=== Збірка моду: AOH2 Very Small Mod ===" -ForegroundColor Cyan
Write-Host "Папка моду: $ScriptDir"
Write-Host "Папка гри:  $GameRoot"

# Пошук javac та jar
$cmdJavac = Get-Command javac -ErrorAction SilentlyContinue
$cmdJar = Get-Command jar -ErrorAction SilentlyContinue

$JavacBin = if ($cmdJavac) { $cmdJavac.Source } else { $null }
$JarBin = if ($cmdJar) { $cmdJar.Source } else { $null }

if (-not $JavacBin) {
    $foundJavac = Get-ChildItem "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java", "C:\Program Files\Microsoft" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($foundJavac) {
        $JavacBin = $foundJavac.FullName
        $JarBin = Join-Path (Split-Path $foundJavac.FullName) "jar.exe"
    }
}

if (-not $JavacBin -or -not (Test-Path $JavacBin)) {
    Write-Error "javac не знайдено! Переконайтеся, що JDK встановлено."
    exit 1
}

if (-not $JarBin -or -not (Test-Path $JarBin)) {
    Write-Error "jar не знайдено! Переконайтеся, що JDK встановлено."
    exit 1
}

Write-Host "Використовуємо javac: $JavacBin" -ForegroundColor Green
Write-Host "Використовуємо jar:   $JarBin" -ForegroundColor Green

# Бекап оригінального game.jar
$GameJar = Join-Path $GameRoot "game.jar"
$GameJarOriginal = Join-Path $GameRoot "game.jar.original"

if (-not (Test-Path $GameJarOriginal)) {
    Write-Host "Створюємо резервну копію game.jar.original..." -ForegroundColor Yellow
    Copy-Item $GameJar $GameJarOriginal
}

# Очищення та підготовка директорії збірки
$BuildDir = Join-Path $ScriptDir "build\classes"
if (Test-Path $BuildDir) {
    Remove-Item -Recurse -Force $BuildDir
}
New-Item -ItemType Directory -Path $BuildDir -Force | Out-Null

$SourcesFile = Join-Path $BuildDir "sources.txt"
$javaFiles = Get-ChildItem -Path (Join-Path $ScriptDir "src") -Filter "*.java" -Recurse
$quotedFiles = $javaFiles | ForEach-Object { '"{0}"' -f $_.FullName.Replace('\', '/') }
[System.IO.File]::WriteAllLines($SourcesFile, $quotedFiles)

Write-Host "Компіляція вихідних файлів під Java 8 (UTF-8)..." -ForegroundColor Cyan
& $JavacBin --release 8 -encoding UTF-8 -Xlint:-options -cp $GameJarOriginal -d $BuildDir "@$SourcesFile"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Помилка компіляції!"
    exit 1
}
Write-Host "Компіляція пройшла успішно!" -ForegroundColor Green

# Відновлюємо ванільний game.jar перед накладанням патчу
Copy-Item -Force $GameJarOriginal $GameJar

# Впроваджуємо модифіковані класи в game.jar
Write-Host "Впроваджуємо класи моду в game.jar..." -ForegroundColor Cyan
& $JarBin -uf $GameJar -C $BuildDir .

if ($LASTEXITCODE -ne 0) {
    Write-Error "Помилка оновлення game.jar!"
    exit 1
}

# Синхронізуємо файли в папку mods гри
$TargetModDir = Join-Path $GameRoot "mods\AOH2-VerySmallMod"
if (Test-Path $TargetModDir) {
    $scriptNorm = (Resolve-Path $ScriptDir).Path
    $targetNorm = (Resolve-Path $TargetModDir).Path
    if ($scriptNorm -ne $targetNorm) {
        Write-Host "Синхронізуємо файли в папку mods гри: $TargetModDir..." -ForegroundColor Cyan
        robocopy $ScriptDir $TargetModDir /E /XD build .git /NFL /NDL /NJH /NJS | Out-Null
    }
}

Write-Host "=== Готово! Мод успішно зібрано та встановлено в гру! ===" -ForegroundColor Green
