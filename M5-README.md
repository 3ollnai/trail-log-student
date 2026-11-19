# ESTIAM E5 — M5 Testing / TrailLog

Source baseline: the user-provided `TrailLog_M4_Teacher_Solution(1).zip`.

## M5 Lab 1 (local JUnit)
Work under `app/src/test/java/fr/estiam/traillog/data/TrailRepositoryLocalTest.kt`.
Test the *existing* `MemoryTrailRepository` against the `TrailRepository` contract (save, read, delete, Flow observation, PagingSource sorting). This is a repository implementation already in the starter, not a newly invented fake DAO. `TrailFormatterTest` already tests KM/MI independently: do not move formatting into the Repository. Sorting in `RoomTrailRepository` is delegated to SQL DAO queries; these JVM tests validate the memory repository's matching behaviour rather than pretending to test Room SQL.

## M5 Lab 2 (Espresso)
Work under `app/src/androidTest/java/fr/estiam/traillog/TrailListEspressoTest.kt`. Real IDs: `trailList`, `addButton`, `nameInput`, `regionInput`, `distanceInput`, `elevationInput`, `saveButton`, `name`. The list uses Paging 3, and the app seeds 30 trails asynchronously from assets. UI tests need deterministic data and appropriate asynchronous synchronization. No invented `trail_list` ID.

## Run
Windows PowerShell:
```
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```
macOS/Linux:
```
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

The second command requires a configured emulator/device. The provided code has not been built or executed in this environment; verify Gradle dependency resolution and execute tests locally before classroom distribution. The teacher version is an example reference to review and adjust if instrumentation behaves differently on the target emulator.
