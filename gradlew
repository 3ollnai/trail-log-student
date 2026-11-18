#!/bin/sh
set -eu
cd "$(dirname "$0")"
if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
  echo 'Gradle wrapper jar missing: run sh setup-wrapper.sh first' >&2
  exit 1
fi
exec java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"
