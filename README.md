# TrailLog — ESTIAM E5 / M5 Student Starter

Android Kotlin + XML Views training project. This branch focuses on **M5 automated testing**. The Room catalogue, JSON seed, DataStore settings, and the M4 fixes form the working application baseline.

## Getting started

1. Open the project folder in Android Studio with JDK 17 and Android SDK API 35 available.
2. Sync Gradle, then start the application on an Android emulator.
3. Read **`M5-EXERCISES.md`** for the two required labs.
4. Build the app: `./gradlew assembleDebug` (Windows: `.\gradlew.bat :app:assembleDebug`).

## M5 labs

- **Lab 1 — Local JUnit 4:** write isolated JVM tests around the existing `MemoryTrailRepository` and `TrailFormatter`; run `:app:testDebugUnitTest`.
- **Lab 2 — Espresso:** write UI tests against `MainActivity` and the trail catalogue; run `:app:connectedDebugAndroidTest` on an online emulator/device.

Existing test methods in the repository may serve as examples, but complete the M5 TODOs yourself. The Espresso starter has an ignored placeholder to allow the test runner to discover the class before you add test methods.

## Important

The starter does **not** define a `TrailRecordStore` interface. Use the actual repository interfaces and implementations in this branch; do not invent an abstraction to match a generic example.

The project has not been built or run in the environment that packaged this ZIP. Run the commands locally and report any errors to the instructor.
