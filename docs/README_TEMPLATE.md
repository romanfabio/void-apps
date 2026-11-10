# README template (mandatory)

Every `apps/<app>/README.md` must use the structure below.

Rules:
- All sections appear in this exact order with these exact headings.
- Every `{{placeholder}}` is replaced. A section with nothing to say contains the single
  line `None.`; sections are never deleted.
- The README is updated in the same commit as the change it describes (new permission,
  new screen, minSdk change, release).
- `apps/_template/README.md` is a filled-in copy of this template and is kept in sync
  with it.

---

```markdown
# Void {{Display Name}}

{{One sentence: what the app does for the user.}}

| | |
|---|---|
| App id (`<app>`) | `{{app}}` |
| Package / applicationId | `io.github.romanfabio.voidapps.{{app}}` |
| Current version | `{{x.y.z}}` (see `version.properties`) |
| minSdk | {{minSdk}} — {{reason if different from the default 28, otherwise "default"}} |
| targetSdk / compileSdk | repository default (see `docs/ANDROID_SUPPORT.md`) |
| Status | {{In development | Released | Deprecated}} |

## Features

- {{Feature 1: one line, user-facing}}
- {{Feature 2}}

## Screens

| Screen | Package | Purpose |
|---|---|---|
| {{Home}} | `feature.{{home}}` | {{one line}} |
| About & licenses | `core.licenses` | App version, MIT license, dependency licenses |

## Permissions

| Permission | Why it is needed | Approved on |
|---|---|---|
| {{android.permission.CAMERA}} | {{Scanning documents}} | {{YYYY-MM-DD}} |

## Data and privacy

- Data stored: {{what, where: Room database `{{app}}.db`, DataStore `{{app}}_preferences`}}
- Data leaving the device: {{None. / description + approval date}}
- Network access: {{None. / description + approval date}}

## App-specific dependencies

Libraries used by this app beyond the shared stack in `CLAUDE.md` §2.

| Library (catalog alias) | License | Why |
|---|---|---|
| {{libs.example}} | {{Apache-2.0}} | {{reason}} |

## Build and run

    ./gradlew :apps:{{app}}:assembleDebug
    ./gradlew :apps:{{app}}:installDebug
    ./gradlew :apps:{{app}}:check

## Release history

| Version | Date | Notes |
|---|---|---|
| {{0.1.0}} | {{YYYY-MM-DD}} | {{Initial version}} |

## Open decisions

- {{⚠️ DECISION NEEDED: … | None.}}
```
