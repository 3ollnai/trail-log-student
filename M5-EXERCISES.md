# TrailLog — M5: Automated Testing Labs

**ESTIAM E5 · Android/Kotlin · Student exercises**

## Learning goals

Write repeatable **JUnit 4 local tests** and **Espresso instrumented UI tests** for the existing TrailLog application. Test production behavior without replacing the application architecture. Use Arrange–Act–Assert, deterministic fixtures, and meaningful assertions.

## Before you start

1. Check out the `m5-student` branch on your own fork, open the project in Android Studio, and synchronize Gradle.
2. Run the app on an emulator. Confirm the trail catalogue opens, and that you can open the add-trail screen.
3. Inspect `TrailRepository`, `MemoryTrailRepository`, `RoomTrailRepository`, `Trail`, `TrailFormatter` and `MainActivity` before writing tests.
4. Keep tests in their appropriate source sets: `app/src/test/java/` for local JVM tests and `app/src/androidTest/java/` for device tests.

> **Starter-specific note:** This repository has **no `TrailRecordStore` interface** and no injectable storage abstraction for `RoomTrailRepository`. For this version, test `MemoryTrailRepository` directly as the existing in-memory implementation of `TrailRepository`; do **not** invent a production interface or introduce a fake Room DAO. The provided baseline can already contain other tests; your task is to add meaningful coverage for the cases below.

---

## Lab 1 — Local JUnit 4 tests: Repository behavior and formatting

**Starter file:** `app/src/test/java/fr/estiam/traillog/data/TrailRepositoryLocalTest.kt`

**Goal:** Verify deterministic repository operations without a device, Room, network access, or filesystem. The existing `MemoryTrailRepository` is the system under test. It maintains in-memory records and implements `TrailRepository`.

### Tasks

1. Create isolated test instances of `MemoryTrailRepository` and known `Trail` fixtures, using stable **String IDs**, such as `pine-1`, `ridge-2`, and `creek-3`. Give them different distances and names (e.g. **Pine Loop** 3.2 km, **Ridge Run** 7.5 km, **Creekside** 1.0 km). The memory repository includes demonstration records by default: use your known IDs and filter or assert on them as appropriate; **do not assume the repository starts empty**.
2. Write tests for `save(trail)` and `getTrail(id)`. Verify that updating an existing String ID changes its data rather than creating a second item with that ID.
3. Verify `delete(id)` and `observeTrails()` using a coroutine test body (`runBlocking`) and a Flow terminal operator such as `first()`.
4. Test the existing **NAME** and **DISTANCE** sorting using `pagingSource(sort, favoritesOnly)` and `PagingSource.LoadParams.Refresh(...)`. Assert the known trails' relative order, not an assumed total number of demonstration items. Also test an edge case such as `favoritesOnly = true` with no matching fixtures, or an unknown ID lookup.
5. In a separate focused local test, test `TrailFormatter.distance(km, unit)` for KM and MI using known expected results; cover a valid boundary such as zero distance. Control `Locale` when asserting exact formatted strings, and restore it afterward.
6. Organize tests with **Arrange → Act → Assert**. Each test must be independent and use meaningful assertions, not only execute code.

**Useful Kotlin APIs:** `org.junit.Test`, `org.junit.Assert`, `runBlocking`, `Flow.first()`, `PagingSource.LoadParams.Refresh(key = null, loadSize = 100, placeholdersEnabled = false)`.

**Run on Windows PowerShell:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**macOS/Linux:** `./gradlew :app:testDebugUnitTest`

**Success criteria:** Tests compile, pass repeatedly, and check save/read/update/delete, observation, sorting, formatting and one boundary condition without using the Android UI or a real database.

---

## Lab 2 — Espresso UI tests: Trail catalogue

**Starter file:** `app/src/androidTest/java/fr/estiam/traillog/TrailListEspressoTest.kt`

**Goal:** Automate a real user journey on an emulator or connected Android device. The launcher Activity is `MainActivity`. Use the existing layout resource IDs; do not invent new screen IDs.

### Tasks

1. Use `@RunWith(AndroidJUnit4::class)` and `ActivityScenarioRule(MainActivity::class.java)` for controlled activity launch.
2. Implement a JUnit `@Test` that asserts `R.id.trailList` is visible with Espresso `onView`, `withId`, `matches`, and `isDisplayed`. An empty list is acceptable when testing only visibility.
3. Implement an add-trail test: tap `R.id.addButton`, enter a deliberately identifiable valid trail into `R.id.nameInput`, `R.id.distanceInput`, and `R.id.elevationInput` (and `R.id.regionInput` if your chosen scenario needs it); then tap `R.id.saveButton`.
4. Confirm that the catalogue is visible again. Find the **specific created trail** in `R.id.trailList` by content, using `RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(...)))` or another appropriately typed content-based action. Assert the matching row's visible content; **do not depend on adapter position**.
5. Account for Room / Flow / Paging 3 asynchronous behavior with deterministic synchronization. Avoid arbitrary `Thread.sleep(...)` calls and test-order dependencies.
6. Design test isolation: avoid false positives from a previous run using the same trail name, and clean up **only the test-created trail** when you have a safe cleanup path. Do not wipe unrelated user data.

The starter includes an **ignored placeholder test** so instrumented test discovery works before you implement your own tests. Replace or remove that placeholder when you add real tests.

**Run on Windows PowerShell (with a device online):**

```powershell
adb devices
.\gradlew.bat :app:connectedDebugAndroidTest
```

**macOS/Linux:** `./gradlew :app:connectedDebugAndroidTest`

`adb devices` must report your target as `device` (not `offline` or `unauthorized`). The project includes AndroidX Test and Espresso dependencies; check existing declarations before adding anything.

**Success criteria:** Your Espresso tests verify catalogue visibility, navigation to the add form, a successful save, and a content-based RecyclerView assertion. They can be rerun without depending on leftover records.

---

## Deliverables

- Your JUnit 4 and Espresso Kotlin test code, committed to your own fork.
- Test output or screenshots showing which test methods ran and passed.
- A short note explaining fixture isolation, asynchronous UI handling, and any limitations encountered.
- The URL of your working Git branch or pull request, as instructed in class.

## Final checklist

- [ ] The app compiles and starts normally.
- [ ] JUnit tests use the existing repository model and include real assertions.
- [ ] KM/MI formatting and NAME/DISTANCE sorting are covered.
- [ ] Espresso uses actual TrailLog IDs and an online emulator/device.
- [ ] RecyclerView assertions use identifiable content, not a hardcoded item index.
- [ ] Tests are repeatable; no arbitrary sleeps or dependence on previous runs.
- [ ] No teacher solutions or instructor-only documents are included in your submission.
