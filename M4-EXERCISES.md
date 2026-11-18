# TrailLog M4: Debugging Labs (Student Starter)

## Before you begin

This branch starts from the **working M3 Room + JSON + DataStore application** and contains two intentional defects. Work on your own GitHub fork and keep the existing app architecture. You should investigate and fix the bugs, not replace the screens or repository.

### Preparation

1. Open the project in Android Studio, synchronize Gradle and launch the application on an emulator.
2. If the project needs a Gradle Wrapper JAR, follow the root `setup-wrapper.ps1` (Windows) or `setup-wrapper.sh` (macOS/Linux) instructions.
3. Confirm the imported trail catalogue loads and you can open a trail.
4. Open **Logcat**, select the correct emulator and application process, and use a filter to isolate relevant messages.

## Lab 1: Diagnose a broken TrailLog feature using the System Log

**Symptom:** The save action unexpectedly refuses a trail with otherwise valid data.

1. Open **Add Trail**.
2. Enter `Mountain Test`, a distance of `8.0` km, and an elevation gain of `750` m. Save.
3. Compare with a similar trail using an elevation gain of `250` m.
4. Reproduce the issue with Logcat open. Filter for the app process; search for save/validation-related messages, including the `TrailLogSave` tag.
5. Identify **the exact condition** responsible for the rejection. Explain why it conflicts with the application's actual model validation.
6. Fix the root cause without removing legitimate checks such as empty names, negative distances or negative elevation.
7. Retest both trails, then force-stop and reopen the app to confirm persistence.

**Submit:** A short explanation of the observed symptom, the relevant Logcat entry, the cause, the changed file, and the verification result. Do not include personal/private log data.

## Lab 2: Set breakpoints and inspect variables to fix a TrailLog bug

**Symptom:** Trail distances are correct in kilometers, but values shown in miles are incorrect.

1. Open Settings and select **MI**.
2. Open an existing trail and compare its distance with the stored kilometer value. For example, `10.0 km` should display approximately `6.2 mi` (not `5.0 mi`).
3. Set a breakpoint in the distance-formatting code used by the screens.
4. Run the app using **Debug**, and inspect the input distance, selected unit, conversion factor, and formatted output.
5. Practice **Step Over**, **Resume**, and the Variables/Evaluate Expression panels.
6. Fix the calculation without changing the persisted `distanceKm` value or breaking KM display.
7. Verify the list and detail screens in both units. Restart the app to verify the saved unit preference.

**Submit:** Where you placed the breakpoint; the values you observed; the root cause; the correction; and before/after screenshots or a short explanation.

## Acceptance criteria

- Both defects are fixed by minimal changes, without removing existing validation.
- The Room catalogue, add/edit/delete flows and DataStore preferences still work.
- Students can explain the Logcat investigation and debugger inspection steps.
- The project builds and the application runs on the emulator.

## Repository workflow

Fork the repository, select the `m4-student` branch, create your working branch, commit your fixes, and share the link with the instructor. **Do not use an instructor solution branch.**
