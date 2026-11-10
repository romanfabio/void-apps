# Void Template

Reference skeleton copied for every new Void app; shows one complete feature (a list of items stored in Room) across all layers.

| | |
|---|---|
| App id (`<app>`) | `template` (folder `apps/_template`, Gradle project `:apps:template`) |
| Package / applicationId | `io.github.romanfabio.voidapps.template` |
| Current version | `0.1.0` (see `version.properties`) |
| minSdk | 28 — default |
| targetSdk / compileSdk | repository default (see `docs/ANDROID_SUPPORT.md`) |
| Status | In development (never released) |

## Features

- List items, newest first.
- Add an item with the floating action button.
- Delete an item, with a snackbar confirmation.

## Screens

| Screen | Package | Purpose |
|---|---|---|
| Home | `feature.home` | Item list with add and delete |
| About & licenses | `core.licenses` | App version, MIT license, dependency licenses |

## Permissions

None.

## Data and privacy

- Data stored: item names in Room database `template.db`.
- Data leaving the device: None.
- Network access: None.

## App-specific dependencies

None.

## Build and run

    ./gradlew :apps:template:assembleDebug
    ./gradlew :apps:template:installDebug
    ./gradlew :apps:template:check

## Release history

| Version | Date | Notes |
|---|---|---|
| 0.1.0 | 2026-11-10 | Initial template |

## Open decisions

- ⚠️ DECISION NEEDED: launcher icon artwork (placeholder ring in use).
