#!/usr/bin/env sh
set -eu

GRADLE_VERSION="8.9"
PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_HOME="$PROJECT_DIR/.gradle-dist/gradle-$GRADLE_VERSION"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
    command -v curl >/dev/null 2>&1 || { echo "Se necesita curl para descargar Gradle." >&2; exit 1; }
    command -v unzip >/dev/null 2>&1 || { echo "Se necesita unzip para descomprimir Gradle." >&2; exit 1; }

    mkdir -p "$PROJECT_DIR/.gradle-dist"
    ZIP="$PROJECT_DIR/.gradle-dist/gradle-$GRADLE_VERSION-bin.zip"
    echo "Descargando Gradle $GRADLE_VERSION..."
    curl -fL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
    unzip -q "$ZIP" -d "$PROJECT_DIR/.gradle-dist"
    rm -f "$ZIP"
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
