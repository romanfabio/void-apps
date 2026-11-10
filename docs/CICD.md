# CI/CD

## 1. Overview

| Stage | Where | Trigger |
|---|---|---|
| Format, static analysis, lint, license check | GitHub Actions `CI` → job `quality` | push to `main`, every pull request |
| Unit + Robolectric tests, coverage | GitHub Actions `CI` → job `test` | same |
| Debug + unsigned release build | GitHub Actions `CI` → job `build` | same |
| Instrumented tests + manual check | **Real attached device**, locally (TESTING.md §7) | every app change, before merging; every release |
| Signing | **Local** (keystores never leave the developer machine) | release |
| Distribution | GitHub Release with the signed APK attached; installed manually or via `adb` | release |

Why signing is local: keystores are stored only on the developer machine
(`docs/SECURITY.md`), so CI cannot sign. ⚠️ DECISION NEEDED (future): moving upload of
keystores to GitHub encrypted secrets would allow a `Release` workflow; until then no
workflow has access to signing material.

## 2. Workflow `CI` — `.github/workflows/ci.yml`

Runs on `ubuntu-latest`, JDK 21 (Temurin), Gradle via `gradle/actions/setup-gradle`
with the build cache enabled.

### Job `changes`
Computes which Gradle modules must be verified, from the diff against the base commit
(`origin/main` for pull requests, the previous commit for pushes):

| Changed path | Modules verified |
|---|---|
| `apps/<app>/**` | `:apps:<app>` |
| `core/<module>/**` | `:core:<module>` **and every app** |
| `build-logic/**`, `gradle/**`, `config/**`, root `*.gradle.kts`, `gradle.properties`, `.github/workflows/**` | every module |
| `docs/**`, `*.md`, `.claude/**`, `keystores/README.md` only | nothing (jobs skipped) |

It also fails if any path forbidden by `docs/SECURITY.md` §2 is tracked by git.

Output: a JSON matrix of Gradle project paths, e.g. `[":apps:scanner", ":core:designsystem"]`.
`apps/_template/**` maps to `:apps:template`, which is treated as an app, so the template
always keeps compiling.

### Job `quality` (matrix over modules, needs `changes`)
`./gradlew <module>:spotlessCheck <module>:detekt <module>:lintDebug`

For app modules it also runs the license check:
`./gradlew <module>:prepareLibraryDefinitionsRelease` (AboutLibraries strict mode; fails on any
license not in the allowlist).

### Job `test` (matrix over modules, needs `changes`)
`./gradlew <module>:testDebugUnitTest <module>:koverVerify`

Uploads `build/reports/tests` and `build/reports/kover` as artifacts on failure.

### Job `build` (matrix over app modules only, needs `quality` and `test`)
`./gradlew <module>:assembleDebug <module>:assembleRelease`

- `assembleRelease` runs R8 and resource shrinking but is **unsigned** in CI.
- The debug APK is uploaded as artifact `void-<app>-debug-<sha7>` with 14-day retention.

### Job `ci-result` (needs all jobs, always runs)
Fails if any of `quality`, `test`, `build` failed or was cancelled; succeeds if they
passed or were skipped. It is the single required status check for `main`, because
matrix job names change with the set of modules.

### Rules
- `main` must be green. A red `main` is fixed before any other work.
- Workflows never print secrets, never use `pull_request_target`, and declare
  `permissions: contents: read` at the top level.
- Third-party actions are pinned to a major version tag (e.g. `@v5`); only these are allowed:
  `actions/checkout`, `actions/setup-java`, `actions/upload-artifact`,
  `gradle/actions/setup-gradle`. Adding another requires approval.
- Concurrency: `group: ci-${{ github.ref }}`, `cancel-in-progress: true`.

## 3. Dependency updates — `.github/dependabot.yml`

- Ecosystems: `gradle` (reads `gradle/libs.versions.toml`) and `github-actions`.
- Schedule: weekly, Monday. Max 5 open PRs.
- Every Dependabot PR is a dependency change: merging it is the owner's approval and must
  pass the license check.

## 4. Release procedure (local)

Follow the `release-app` skill; it implements exactly these steps.

1. On an up-to-date `main` with a clean working tree.
2. Run `./gradlew :apps:<app>:check` — must pass.
3. Run the real-device procedure (TESTING.md §7) on every available real device, covering the
   app's minSdk and targetSdk where such devices exist (ANDROID_SUPPORT.md §5) — must pass.
4. Bump `apps/<app>/version.properties` (SemVer), add a row to the README
   "Release history", commit `chore(<app>): release v<version>`.
5. Run `./gradlew :apps:<app>:exportReleaseApk`. This builds the signed release APK,
   verifies its signature with `apksigner`, and writes
   `apps/<app>/build/outputs/void/void-<app>-<version>.apk`.
6. Tag `<app>/v<version>` on the release commit and push the commit and tag.
7. Create the GitHub Release:
   `gh release create <app>/v<version> <apk> --title "Void <Display Name> <version>" --notes-file <notes>`.
8. Install: `adb install -r <apk>`.

The signing step fails if `keystores/<app>.jks` or `keystores/keystore.properties` is
missing; it never falls back to the debug key.

## 5. Branch flow

- Single developer, trunk-based: work on a short-lived branch (`docs/NAMING.md` §7), open
  a pull request to `main`, merge with **squash** once `CI` is green.
- Branch protection on `main`: require status check `ci-result`; require linear history. ⚠️ DECISION NEEDED: enable these settings once the GitHub repository
  exists (they are configured in GitHub, not in this repository).
