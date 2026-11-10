# Android support

## 1. Current values

| Setting | Value | Defined in |
|---|---|---|
| `compileSdk` | **37** (Android 17) | `VoidSdk.kt`¹ |
| `targetSdk` | **37** (Android 17) | `VoidSdk.kt`¹ |
| Default app `minSdk` | **28** (Android 9) | `VoidSdk.kt`¹ |
| Core module `minSdk` (floor) | **26** (Android 8.0) | `VoidSdk.kt`¹ |
| JDK / JVM target | 21 | `VoidSdk.kt`¹ |
| Form factor | Phones; tablets/foldables via adaptive layouts | `DESIGN_SYSTEM.md` §7 |

¹ `build-logic/convention/src/main/kotlin/io/github/romanfabio/voidapps/buildlogic/VoidSdk.kt`

Tooling versions (AGP, Kotlin, Gradle, libraries) live only in
`gradle/libs.versions.toml` and `gradle/wrapper/gradle-wrapper.properties`.

## 2. Why these values

- **targetSdk = compileSdk = latest stable.** Apps are side-loaded, so Google Play's
  target-level deadlines do not apply. Targeting the latest level keeps the newest
  privacy and security behaviours on, and new apps start without migration debt.
- **Default minSdk 28.** Android 9+ covers the vast majority of active devices and
  removes legacy code paths (pre-28 biometric APIs, cleartext-by-default networking,
  older notification and background limits).
- **Floor 26 for core modules.** Libraries must not be more restrictive than the most
  permissive app; 26 is the lowest level an app may choose (adaptive icons, notification
  channels and `java.time` are available from 26).

## 3. Per-app minSdk

Each app chooses its own `minSdk`:

```kotlin
// apps/<app>/build.gradle.kts
android {
    defaultConfig { minSdk = 31 } // omit to use the default (28)
}
```

Rules:
1. Allowed range: **26 ≤ minSdk ≤ targetSdk**. The `void.android.application` plugin fails
   the build outside this range.
2. Any value other than 28 is justified in the app README header (`minSdk` row), e.g.
   "31 — requires `SplashScreen` API and exact-alarm permission model".
3. **Raising** the minSdk of an app that has already been released requires approval:
   devices below the new level stop receiving updates.
4. Code guarded by `Build.VERSION.SDK_INT` checks is allowed only for levels between the
   app's minSdk and targetSdk; Lint `NewApi` and `ObsoleteSdkInt` are errors.

## 4. Update policy

| Event | Action | Deadline |
|---|---|---|
| New stable Android release **and** a stable AGP that supports it | Bump `compileSdk` for the whole repo | 30 days |
| Same | Bump `targetSdk` for the whole repo after reviewing the platform's "behavior changes: apps targeting N" page; fixes land in the same PR | 90 days |
| Every January | Review the core floor and default minSdk; propose changes to the owner | January 31 |
| New stable AGP / Kotlin / Compose BOM | Bump via Dependabot PR (`CICD.md` §3) | next working session |

- `compileSdk` and `targetSdk` are always the same for every app; there are no per-app
  overrides.
- Only **stable** releases of tools and libraries are allowed: no version in the catalog
  may contain `alpha`, `beta`, `rc`, `dev`, or `SNAPSHOT`.
- SDK changes are a single commit `build(repo): target sdk <N>` with the approval line.

## 5. Test devices

All on-device testing uses **real attached devices**; emulators do not count
(`TESTING.md` §7). Before every release instrumented tests run on:
1. A real device at the app's **minSdk** (or the lowest-API real device available).
2. A real device at **targetSdk** (or the highest-API real device available).

Device inventory (update when devices change):

| Model | Android | API | Form factor |
|---|---|---|---|
| Realme RMX3890 | 15 | 35 | Phone (compact) |

⚠️ DECISION NEEDED: no real device at API 28 (default minSdk) or API 37 (targetSdk), and no
tablet/foldable for expanded-width layouts. Gaps are stated in each release's notes.
