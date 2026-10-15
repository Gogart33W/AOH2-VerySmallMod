#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GAME_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "=== Відновлення оригінальної версії гри (Vanilla) ==="

if [ -f "$GAME_ROOT/game.jar.original" ]; then
    cp -p "$GAME_ROOT/game.jar.original" "$GAME_ROOT/game.jar"
    echo "Успішно відновлено чистий game.jar з бекапу!"
else
    echo "Помилка: $GAME_ROOT/game.jar.original не знайдено!"
    exit 1
fi

echo "Оригінальні файли гри повернено."
