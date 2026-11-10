# Void apps

A family of small, private, offline-first Android apps under the **Void** brand.
Each app lives in its own folder under `apps/` and shares one design system and one
set of rules.

| App | Folder | Status |
|---|---|---|
| _(none yet)_ | | |

## Principles

- No tracking, no analytics, no ads. Data stays on the device.
- Consistent look and behaviour across all apps (Material 3, Void palette, dark theme).
- Open source under the [MIT license](LICENSE); every app shows its license and the
  licenses of its dependencies.

## Build

Requirements: JDK 21 (the one bundled with Android Studio works) and the Android SDK.

    ./gradlew check assembleDebug

## Documentation

Start with [`CLAUDE.md`](CLAUDE.md), then the rules in [`docs/`](docs/).
