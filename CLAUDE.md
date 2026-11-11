# CLAUDE.md — Void apps monorepo

This file is the entry point for any model or developer working in this repository.
Read it fully before changing anything. The rules in `docs/` are binding; this file
summarises them and links to them.

## 1. What this repository is

- **Brand:** Void. Every app's user-visible name is `Void <Name>` (e.g. `Void Scanner`).
- **Content:** a family of Android apps, one per folder under `apps/`, sharing one Gradle
  build, one version catalog, one design system, and one set of rules.
- **Platform:** Android phones (large screens supported via `WindowSizeClass`). No iOS,
  no Wear OS, no TV, no web.
- **Backend:** apps are offline-first and have **no backend** by default. Adding network
  access to any app requires approval (see §6).
- **Analytics / crash reporting:** **none**. No Firebase, no telemetry SDKs, no
  third-party tracking of any kind.
- **Distribution:** signed APKs installed manually or via `adb`. No Google Play.
- **License:** the repository is **MIT** (`LICENSE` at the root). Every dependency must
  be under a permissive license (see §5).
- **Remote:** `https://github.com/romanfabio/void-apps`.
- **Team:** one developer. Trunk-based development on `main`.

## 2. Technology stack (fixed)

| Area | Choice |
|---|---|
| Language | Kotlin (JVM toolchain 21) |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM with unidirectional data flow, layers `ui` / `domain` / `data` |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose with type-safe `@Serializable` routes |
| Persistence | Room (structured data), DataStore Preferences (settings) |
| Serialization | kotlinx.serialization |
| Licenses screen | AboutLibraries (Gradle plugin + Compose M3 UI) |
| Build | Gradle Kotlin DSL, `gradle/libs.versions.toml`, convention plugins in `build-logic/` |
| Quality | Spotless + ktlint, detekt, Android Lint, Kover |
| CI | GitHub Actions |
| SDK | compileSdk / targetSdk / default minSdk defined in `docs/ANDROID_SUPPORT.md` |

Rationale for each choice lives in the relevant doc. Do not introduce alternatives
(Koin, Retrofit, RxJava, XML layouts, Groovy build scripts, kapt, LiveData, etc.).

## 3. Repository map

```
void-apps/
├── CLAUDE.md                 ← you are here
├── LICENSE                   ← MIT
├── README.md                 ← short public overview
├── settings.gradle.kts       ← includes every module (core/*, apps/*)
├── build.gradle.kts          ← root: plugin declarations only
├── gradle.properties
├── gradle/libs.versions.toml ← the ONLY place versions are declared
├── build-logic/convention/   ← convention plugins (void.android.application, …)
├── config/detekt/detekt.yml  ← shared detekt rules
├── core/
│   ├── designsystem/         ← VoidTheme, colors, typography, spacing, components
│   ├── common/               ← dispatchers, shared utilities
│   ├── licenses/             ← shared "About & licenses" screen
│   └── testing/              ← test rules and helpers
├── apps/
│   ├── _template/            ← skeleton copied for every new app; Gradle project :apps:template
│   └── <app>/                ← one folder per app
├── keystores/                ← local signing keys (git-ignored except README.md)
├── docs/                     ← binding rules (see §4)
├── .claude/skills/           ← reusable task procedures (see §7)
└── .github/workflows/        ← CI
```

Where to look first:
- Changing an app → `apps/<app>/` and its `README.md`.
- Changing anything shared → read §6 first.
- Unsure how to name something → `docs/NAMING.md`.

## 4. Binding documents

| Document | Covers |
|---|---|
| `docs/ARCHITECTURE.md` | Module layout, package structure, layers, data flow |
| `docs/NAMING.md` | Folders, packages, `applicationId`, files, code, branches, commits, tags |
| `docs/DESIGN_SYSTEM.md` | Colors, typography, spacing, shapes, components, accessibility |
| `docs/TESTING.md` | Test types, locations, coverage thresholds |
| `docs/CICD.md` | Workflows, jobs, release procedure |
| `docs/ANDROID_SUPPORT.md` | SDK levels and update policy |
| `docs/SECURITY.md` | Keystores, secrets, what must never be committed |
| `docs/README_TEMPLATE.md` | Mandatory structure of every `apps/<app>/README.md` |

If a rule in a doc conflicts with this file, the doc wins and this file must be fixed.

## 5. General rules (all apps, always)

1. **Identity.** Package and `applicationId` are `io.github.romanfabio.voidapps.<app>`,
   where `<app>` matches `^[a-z][a-z0-9]*$` and equals the folder name under `apps/`.
2. **Display name.** `app_name` string resource is `Void <Name>`; debug builds append
   ` (debug)`.
3. **Language.** English only. All user-visible text lives in `res/values/strings.xml`;
   no hard-coded strings in composables.
4. **Theme.** Every screen is wrapped in `VoidTheme` from `core/designsystem`. Dark and
   light themes are both mandatory; dynamic color is **disabled**. No colors, font sizes
   or dimensions defined inside an app — use design-system tokens.
5. **Licenses section.** Every app has an "About & licenses" entry reachable from its
   main navigation in at most two taps. It shows the app version, the MIT license text,
   and the list of all dependency licenses, using `core/licenses`.
6. **Permissive dependencies only.** Allowed SPDX licenses: `Apache-2.0`, `MIT`,
   `BSD-2-Clause`, `BSD-3-Clause`, `ISC`. Anything else (GPL, LGPL, AGPL, MPL, EPL, CDDL,
   proprietary, unknown) is forbidden. The AboutLibraries plugin enforces this at build
   time; a build that fails the license check must not be bypassed.
7. **Dependencies** are declared only in `gradle/libs.versions.toml` and referenced via
   `libs.*`. No version literals in any `build.gradle.kts`.
8. **No network by default.** Apps do not declare `android.permission.INTERNET` unless
   approved and documented in the app README.
9. **No tracking.** No analytics, crash reporting, ads, or telemetry SDKs.
10. **Tests and quality gates** in `docs/TESTING.md` must pass before merging to `main`,
    including the **real-device** procedure (`docs/TESTING.md` §7). Emulators never count.
11. **Secrets** never enter git (see `docs/SECURITY.md`).
12. **Per-app README** must follow `docs/README_TEMPLATE.md` and be updated in the same
    change that alters what it describes.
13. **Git identity.** Every commit (author and committer) is
    `Fabio Roman <61688500+romanfabio@users.noreply.github.com>`. Set it with
    `git config --local` and verify `git config user.email` before committing; never use
    another email address (`docs/NAMING.md` §7, "Commit identity").

## 6. Changes that require explicit approval from the owner

A model must stop and ask before modifying any of the following. A developer must
record the reason in the commit body.

- `CLAUDE.md`, anything under `docs/`, and `LICENSE`.
- `gradle/libs.versions.toml` (adding, removing, or bumping any dependency or plugin).
- `build-logic/**`, root `settings.gradle.kts` (except registering a new app),
  root `build.gradle.kts`, `gradle.properties`, Gradle wrapper files.
- `core/designsystem/**` (tokens, theme, shared components).
- `core/licenses/**`, `core/common/**`, `core/testing/**`.
- `config/detekt/detekt.yml`, `.editorconfig`, lint baselines, Kover thresholds.
- `.github/**`.
- `.gitignore`, anything under `keystores/`, signing configuration.
- compileSdk, targetSdk, the core minSdk floor, or any app's minSdk.
- Adding a permission to any `AndroidManifest.xml`.
- Adding network access, a backend, or any third-party service.
- Deleting or renaming an app, or changing an existing app's `applicationId`
  (this breaks updates of installed APKs).
- `.claude/skills/**`.

Changes inside a single `apps/<app>/src/**` that follow the docs do **not** need approval.

## 7. Skills

Use the skill instead of improvising when the task matches:

| Skill | Use when |
|---|---|
| `new-app` | Creating a new app from `apps/_template` |
| `add-screen` | Adding a screen/feature to an existing app |
| `add-dependency` | Adding or bumping any library or plugin |
| `verify` | Before every commit: runs all quality gates for one app |
| `release-app` | Producing a signed release APK and GitHub Release |

## 8. Working procedure for models

1. Identify the target app folder; read its `README.md`.
2. Check §6 — if the change touches a protected path, ask first.
3. Implement following `docs/ARCHITECTURE.md` and `docs/NAMING.md`.
4. Add or update tests per `docs/TESTING.md`.
5. Run the `verify` skill, including the real-attached-device step, and report the real
   result (device model, Android version, API level). No device attached → stop and ask.
6. Check `git config user.email` prints the noreply address (§5 rule 13), then commit
   using the Conventional Commits format in `docs/NAMING.md`.

Never invent a decision that this file or `docs/` marks as **⚠️ DECISION NEEDED**;
ask the owner instead.

## 9. Open decisions

- ⚠️ DECISION NEEDED: off-repository backup location for `keystores/` (see
  `docs/SECURITY.md`).
- ⚠️ DECISION NEEDED: brand font. Until decided, typography uses the system default
  font family (Roboto) via `VoidTypography`.
- ⚠️ DECISION NEEDED: app launcher icon artwork. The template ships a placeholder
  adaptive icon.
- ⚠️ DECISION NEEDED: real test devices at API 28 (default minSdk) and API 37
  (targetSdk), and a large-screen device. Today only one phone at API 35 is available
  (`docs/ANDROID_SUPPORT.md` §5).

## 10. Local environment

- JDK: the JetBrains Runtime bundled with Android Studio
  (`C:\Program Files\Android\Android Studio\jbr` on the owner's machine). Set
  `JAVA_HOME` to it, or select it in Android Studio → Gradle JDK.
- Android SDK: path in `local.properties` (`sdk.dir=…`), never committed.
- Build everything: `./gradlew check assembleDebug`.
