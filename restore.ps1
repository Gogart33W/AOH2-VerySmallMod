$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

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

Write-Host "=== Відновлення оригінальної версії гри (Vanilla) ===" -ForegroundColor Cyan

$GameJar = Join-Path $GameRoot "game.jar"
$GameJarOriginal = Join-Path $GameRoot "game.jar.original"

if (Test-Path $GameJarOriginal) {
    Copy-Item -Force $GameJarOriginal $GameJar
    Write-Host "Успішно відновлено чистий game.jar з бекапу!" -ForegroundColor Green
} else {
    Write-Error "Помилка: $GameJarOriginal не знайдено!"
    exit 1
}
