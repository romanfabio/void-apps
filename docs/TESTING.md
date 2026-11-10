# Testing

## 1. Test stack

| Purpose | Library | License |
|---|---|---|
| Test framework | JUnit 4 | EPL-1.0 ⚠️ see note |
| Assertions | `kotlin-test` | Apache-2.0 |
| Coroutines | `kotlinx-coroutines-test` | Apache-2.0 |
| Flow assertions | Turbine | Apache-2.0 |
| JVM Android runtime | Robolectric | MIT |
| Compose UI tests | `androidx.compose.ui:ui-test-junit4` | Apache-2.0 |
| Instrumentation | `androidx.test` runner/rules/ext-junit | Apache-2.0 |
| Hilt in tests | `hilt-android-testing` | Apache-2.0 |
| Coverage | Kover | Apache-2.0 |

Note: JUnit 4 is EPL-1.0. Test dependencies are **not shipped in the APK** and are excluded
from the runtime license allowlist in `CLAUDE.md` §5, which applies to dependencies on
the `releaseRuntimeClasspath`. This is the only exception. ⚠️ DECISION NEEDED: confirm
that EPL-licensed **test-only** dependencies are acceptable; if not, JUnit 4 must be
replaced across the repository.

Why JUnit 4: it is the framework natively supported by AGP, Robolectric, Compose UI tests
and AndroidX Test without extra plugins, so one framework covers every test type.

Forbidden: mocking frameworks (MockK, Mockito). Use hand-written fakes — they keep tests
readable and survive refactors.

## 2. Test types and locations

| Type | Source set | Runs on | What it covers | Command |
|---|---|---|---|---|
| Unit | `src/test/` | JVM | ViewModels, use cases, repositories (with fakes), mappers, utilities | `./gradlew :apps:<app>:testDebugUnitTest` |
| Compose UI (JVM) | `src/test/` | JVM + Robolectric | Stateless `<Screen>Screen` composables, design-system components | same as above |
| Room DAO | `src/test/` | JVM + Robolectric, in-memory DB | DAO queries | same as above |
| Instrumented | `src/androidTest/` | **Real attached device** (§7) | Room migrations, one end-to-end smoke test per app | `./gradlew :apps:<app>:connectedDebugAndroidTest` |
| Manual on-device check | — | **Real attached device** (§7) | Install, launch, exercise every changed screen | `./gradlew :apps:<app>:installDebug` |

- Compose UI tests run on Robolectric so they execute in CI without an emulator.
- Every module with Robolectric tests has `src/test/resources/robolectric.properties`
  containing `sdk=36`: Robolectric 4.17 cannot yet emulate API 37. Raise it when a stable
  Robolectric supports the targetSdk. The convention plugins add the JVM flags Robolectric
  needs on JDK 21 (`--add-opens java.base/java.io`, `--add-exports
  java.base/jdk.internal.access`).
- Compose tests use the v2 rules: `androidx.compose.ui.test.junit4.v2.createComposeRule`
  and `…v2.createAndroidComposeRule`.
- Instrumented tests are **not** run in CI (CI has no real devices). They are run locally
  on real attached devices for every app change (§7).

Directory layout mirrors `src/main/`:

```
apps/<app>/src/
├── main/java/io/github/romanfabio/voidapps/<app>/feature/history/ui/HistoryViewModel.kt
├── test/java/io/github/romanfabio/voidapps/<app>/feature/history/ui/HistoryViewModelTest.kt
├── test/java/io/github/romanfabio/voidapps/<app>/feature/history/ui/HistoryScreenTest.kt
├── test/java/io/github/romanfabio/voidapps/<app>/feature/history/data/FakeHistoryRepository.kt
└── androidTest/java/io/github/romanfabio/voidapps/<app>/data/local/MigrationTest.kt
```

Fakes used by several tests of the same app live in `src/test/.../<feature>/data/`;
fakes and rules used by several apps live in `:core:testing`.

## 3. What must be tested

| Production class | Required tests |
|---|---|
| `<Screen>ViewModel` | Initial state; every `<Screen>Action` branch; every emitted `<Screen>Event`; error path |
| `<Verb><Noun>UseCase` | Every branch |
| `Default<Noun>Repository` | Mapping entity ↔ model; error → `VoidResult.Failure` |
| `<Noun>Dao` | Every query with a `WHERE`, `ORDER BY` or `JOIN` |
| `<Screen>Screen` | Renders each UiState variant; each clickable element triggers the right `Action` |
| Room schema version N → N+1 | One migration test per migration |
| App as a whole | One smoke test: app launches, main screen visible, About & licenses reachable |
| `Void*` design-system component | Renders; click/selection callbacks; content description present |

## 4. How tests are written

- Test function names in `src/test/`: backtick sentences,
  `` fun `refresh action loads items into success state`() ``.
- Test function names in `src/androidTest/`: camelCase `methodOrAction_condition_result`
  (spaces are not allowed in method names on older Android runtimes).
- Structure each test as arrange / act / assert, separated by a blank line.
- ViewModel tests use `MainDispatcherRule` from `:core:testing` and `runTest`.
- Flows are asserted with Turbine (`flow.test { … }`); `Thread.sleep` and `delay`-based
  waiting are forbidden.
- No test depends on another test's state or on execution order.
- No test accesses the network or the real file system outside Robolectric's sandbox.

## 5. Coverage

Tool: **Kover**, configured by the `void.quality` convention plugin.

| Scope | Minimum line coverage |
|---|---|
| Classes named `*ViewModel`, `*UseCase`, `Default*Repository` in each app | **80%** |
| `:core:common` | **80%** |
| Composables, `MainActivity`, `*Application`, Hilt modules, generated code | excluded |

- Enforced by `./gradlew :apps:<app>:koverVerify`, which is part of `check`.
- Lowering a threshold or widening exclusions requires approval (`CLAUDE.md` §6).
- HTML report: `./gradlew :apps:<app>:koverHtmlReport` →
  `apps/<app>/build/reports/kover/html/index.html`.

## 6. Definition of done for any change

1. `./gradlew :apps:<app>:check` passes (unit + Robolectric tests, Lint, detekt, Spotless,
   Kover).
2. New or changed behaviour has tests per §3.
3. For every change to an app, or to a `core` module it uses: the real-device procedure in
   §7 passes, and its result is reported (device model, Android version, API level).

## 7. Real-device testing (mandatory)

Apps are **always** tested on real devices attached through USB (or `adb` over Wi-Fi).
Emulators never satisfy this rule, and a passing JVM/Robolectric run does not replace it.

### When
- Every change to `apps/<app>/**`: before committing to the branch that gets merged.
- Every change to `core/**` or `build-logic/**`: for every app.
- Every release: on every available real device (`CICD.md` §4).

### How to confirm a device is real

```bash
adb devices -l                                          # state must be "device"
adb -s <serial> shell getprop ro.kernel.qemu            # empty or 0 → real; 1 → emulator
adb -s <serial> shell getprop ro.build.version.sdk      # API level
adb -s <serial> shell getprop ro.product.model          # model
```

If only emulators (or no devices) are attached: **stop** and ask the owner to attach a
device. Do not skip the step and do not substitute an emulator. When emulators and real
devices are both attached, target the real one with `ANDROID_SERIAL=<serial>`.

### Procedure
1. `./gradlew :apps:<app>:connectedDebugAndroidTest` — all instrumented tests pass on the
   device. Results: `apps/<app>/build/outputs/androidTest-results/connected/`.
2. `./gradlew :apps:<app>:installDebug`, launch the app, and exercise every screen the change
   touches plus the About & licenses screen: each state (loading/empty/content/error where
   reachable), the main actions, back navigation, and data surviving
   `adb shell am force-stop <applicationId>.debug` + relaunch.
3. Check the screens in the theme the device is set to, and in the other theme
   (`adb shell cmd uimode night yes|no`, restoring the original value afterwards).
4. Report model, Android version, API level, and what was checked. Screenshots
   (`adb exec-out screencap -p`) go to a scratch folder, never into the repository.

### Respect the device
The device is the owner's personal phone. Interact only with the app under test: no taps
or screenshots of other apps, notifications or settings; collapse the notification shade
(`adb shell cmd statusbar collapse`) before taking a screenshot; do not change system
settings except temporary night mode, restored at the end; never uninstall another app.

### Coverage of SDK levels
The release rule in `ANDROID_SUPPORT.md` §5 requires devices at the app's minSdk and at
targetSdk. ⚠️ DECISION NEEDED: the only device available today runs API 35, which is
neither the default minSdk (28) nor the targetSdk (37). Until more real devices are
available, releases are tested on the available device(s) and the gap is stated in the
release notes.
