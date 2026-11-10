---
name: verify
description: Run every quality gate (format, detekt, lint, license allowlist, unit/Robolectric tests, coverage, build) for the modules affected by the current change and report the real results. Use before every commit and whenever asked to check, test, or validate code.
---

# verify

## 1. Environment

- `JAVA_HOME` must point to a JDK 21. On the owner's Windows machine use Android Studio's:
  - Git Bash: `export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"`
  - PowerShell: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`
- `local.properties` with `sdk.dir` must exist (never committed).

## 2. Pick the modules (same rule as CI, docs/CICD.md §2)

| Changed | Verify |
|---|---|
| `apps/<app>/**` | `:apps:<app>` (`apps/_template` → `:apps:template`) |
| `core/<module>/**` | `:core:<module>` and every app |
| `build-logic/**`, `gradle/**`, `config/**`, root Gradle files | everything (`./gradlew check assembleDebug`) |
| docs only | nothing to build; check links and section numbers by reading |

## 3. Run

Format first (it rewrites files; review the diff):

```bash
./gradlew <module>:spotlessApply
```

Then, for each app module:

```bash
./gradlew <module>:check <module>:assembleDebug <module>:assembleRelease <module>:prepareLibraryDefinitionsRelease
```

For core modules: `./gradlew <module>:check`.

`check` includes `spotlessCheck`, `detekt`, `lintDebug`, unit + Robolectric tests for debug
and release, and `koverVerify` (80% on ViewModels, use cases and `Default*Repository`;
80% on `:core:common`).

## 3b. Real attached device (mandatory for every app change — docs/TESTING.md §7)

Applies to every affected app (a `core`/`build-logic` change affects all apps).

```bash
adb devices -l
adb -s <serial> shell getprop ro.kernel.qemu         # must be empty or 0 (real device)
adb -s <serial> shell getprop ro.build.version.sdk
```

- No real device attached → **stop**, tell the owner, and report verification as
  incomplete. Never substitute an emulator. If emulators are also attached, export
  `ANDROID_SERIAL=<real serial>` first.
- Run `./gradlew <module>:connectedDebugAndroidTest` (all must pass).
- Run `./gradlew <module>:installDebug`, then launch with
  `adb shell am start -W -n <applicationId>.debug/<applicationId>.MainActivity` and exercise
  each changed screen with `adb shell uiautomator dump` + `adb shell input tap/swipe`,
  checking states, actions, back navigation, About & licenses, data persistence after
  `am force-stop`, and both themes (`cmd uimode night yes|no`, restore afterwards).
- Before every screenshot run `adb shell cmd statusbar collapse`; only capture the app
  under test; save screenshots to a scratch folder, never the repository.
- In Git Bash, set `MSYS_NO_PATHCONV=1` so device paths like `/sdcard/...` are not rewritten.

## 4. Report

- Report the actual outcome: which tasks passed, which failed, with the first relevant
  error lines. Never say "should pass".
- Report locations: `build/reports/tests/`, `build/reports/lint-results-debug.html`,
  `build/reports/detekt/`, `build/reports/kover/html/index.html`.
- Do not "fix" failures by suppressing rules, lowering thresholds, adding lint baselines,
  or excluding files from coverage — those are protected changes (CLAUDE.md §6).
