#!/usr/bin/env bash
set -e

# Шлях до кореня гри та папки моду
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GAME_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=== Збірка моду: AOH2 Very Small Mod ==="
echo "Папка гри: $GAME_ROOT"

# Знаходимо javac
JAVAC_BIN=""
if [ -x "/usr/lib/jvm/java-21-openjdk/bin/javac" ]; then
    JAVAC_BIN="/usr/lib/jvm/java-21-openjdk/bin/javac"
elif command -v javac >/dev/null 2>&1; then
    JAVAC_BIN="$(command -v javac)"
else
    echo "Помилка: javac не знайдено! Встановіть JDK."
    exit 1
fi

JAR_BIN=""
if [ -x "/usr/lib/jvm/java-21-openjdk/bin/jar" ]; then
    JAR_BIN="/usr/lib/jvm/java-21-openjdk/bin/jar"
elif command -v jar >/dev/null 2>&1; then
    JAR_BIN="$(command -v jar)"
else
    echo "Помилка: jar не знайдено! Встановіть JDK."
    exit 1
fi

echo "Використовуємо компилятор: $JAVAC_BIN"

# Переконуємось, що є недоторканний бекап оригінального game.jar
if [ ! -f "$GAME_ROOT/game.jar.original" ]; then
    echo "Створюємо безпечну копію game.jar.original..."
    cp -p "$GAME_ROOT/game.jar" "$GAME_ROOT/game.jar.original"
fi

# Очищуємо та готуємо папку збірки
BUILD_DIR="$SCRIPT_DIR/build/classes"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

# Компіляція вихідних файлів під Java 8
echo "Компілюємо файли моду..."
SOURCES_FILE="$BUILD_DIR/sources.txt"
find "$SCRIPT_DIR/src" -name "*.java" | sed 's/^/"/;s/$/"/' > "$SOURCES_FILE"

"$JAVAC_BIN" --release 8 -Xlint:-options \
    -cp "$GAME_ROOT/game.jar.original" \
    -d "$BUILD_DIR" \
    @"$SOURCES_FILE"

echo "Компіляція пройшла успішно!"

# Відновлюємо ванільний game.jar перед накладанням патчу, щоб уникнути нашарування
cp -p "$GAME_ROOT/game.jar.original" "$GAME_ROOT/game.jar"

# Інжектуємо скомпільовані класи моду в game.jar
echo "Впроваджуємо модифіковані класи в game.jar..."
(cd "$BUILD_DIR" && $JAR_BIN -uf "$GAME_ROOT/game.jar" .)

echo "=== Готово! Мод успішно встановлено в гру! ==="
echo "Для повного відкату до чистої гри запусти ./restore.sh"
