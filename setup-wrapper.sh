#!/bin/sh
set -eu
cd "$(dirname "$0")"
if [ -f gradle/wrapper/gradle-wrapper.jar ]; then
  echo 'Gradle Wrapper already present.'
  exit 0
fi
VERSION=8.13
CACHE="${HOME}/.gradle/traillog-bootstrap"
ARCHIVE="$CACHE/gradle-$VERSION-bin.zip"
GRADLE="$CACHE/gradle-$VERSION/bin/gradle"
mkdir -p "$CACHE"
if [ ! -f "$GRADLE" ]; then
  if [ ! -f "$ARCHIVE" ]; then
    echo "Downloading official Gradle $VERSION distribution..."
    curl --fail --location --show-error "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ARCHIVE"
  fi
  unzip -oq "$ARCHIVE" -d "$CACHE"
fi
TMP="$CACHE/wrapper-generator"
mkdir -p "$TMP"
printf "rootProject.name = 'wrapper-generator'\n" > "$TMP/settings.gradle"
: > "$TMP/build.gradle"
"$GRADLE" -p "$TMP" wrapper --gradle-version "$VERSION" --distribution-type bin
mkdir -p gradle/wrapper
cp "$TMP/gradle/wrapper/gradle-wrapper.jar" gradle/wrapper/gradle-wrapper.jar
echo 'Gradle Wrapper ready. Run ./gradlew assembleDebug'
