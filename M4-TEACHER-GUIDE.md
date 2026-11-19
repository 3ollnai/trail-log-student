# TrailLog M4: Instructor Guide and Answer Key

## Branch design

- `m4-student`: M3 solution baseline with two deliberate independent defects.
- `m4-solution`: Same M3 baseline with both defects corrected (no deliberate bugs).
- The two projects share identical Room, JSON and DataStore architecture. Do not publish the teacher guide on the student branch.

## Lab 1: System Log

**Defect location:** `app/src/main/java/fr/estiam/traillog/ui/EditTrailActivity.kt`, inside the Save click handler, immediately before the `Trail` value is created.

**Student-branch-only code to remove:**

```kotlin
if (elevation > 500) {
    Log.e("TrailLogSave", "Save rejected during elevation validation: elevationGainM=$elevation")
    Snackbar.make(b.root, R.string.invalid_fields, Snackbar.LENGTH_LONG).show()
    return@setOnClickListener
}
```

Also remove `import android.util.Log` when unused. Existing standard validation already rejects blank names, non-finite or negative distances, and negative elevation. `Trail.isValid()` imposes no upper limit of 500 m on elevation gain.

**Live demonstration:** Add a trail called `Mountain Test` with 8.0 km and 750 m gain; observe rejection and filter Logcat with `tag:TrailLogSave`. Put a breakpoint or inspect the conditional if desired. Remove the faulty block and rerun: the same input now saves, while invalid negative values are still rejected.

**Explain:** Logging is evidence, not a substitute for understanding the control flow. An arbitrary UI-only validation rule contradicts the domain model.

## Lab 2: Breakpoints and Variables

**Defect location:** `app/src/main/java/fr/estiam/traillog/model/UserSettings.kt` in `TrailFormatter.distance()`.

**Student-branch faulty expression:**
```kotlin
km * 0.5
```

**Correct expression:**
```kotlin
km * 0.621371
```

**Live demonstration:** Use `10.0 km` as test input, select MI in Settings, then open a trail or stop in `TrailFormatter.distance()` during UI binding. Inspect `km`, `unit` and the expression. Expected: 10 km = 6.2 mi (one decimal), not 5.0 mi. Step over and resume to inspect results. Both the catalogue and detail screen use the same formatter.

A local regression test `TrailFormatterTest.kt` is included in the teacher project. On the student branch, you may invite students to write their own tests, but do not give them the solution test initially if it reveals the exact factor.

## Validation before distribution

Run `gradlew.bat assembleDebug` and `gradlew.bat testDebugUnitTest` (or `./gradlew` on macOS/Linux) after the Gradle wrapper setup. Validate on Android emulator:
1. Seeded catalogue appears.
2. Student branch rejects 750 m elevation and emits tagged Logcat message; teacher version saves it.
3. Student branch displays 10 km as 5.0 mi; teacher version shows 6.2 mi.
4. Existing Room CRUD and DataStore persistence remain intact.

**Important:** The source archives have been edited and checked structurally, but builds and emulator tests have not been executed in this environment.
