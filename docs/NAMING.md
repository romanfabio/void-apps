# Naming conventions

Every rule here is checkable by reading a name. Placeholders:

- `<app>` — app identifier: `^[a-z][a-z0-9]*$`, max 20 chars, e.g. `scanner`, `notes2`.
- `<Name>` — `<app>` in PascalCase, e.g. `Scanner`.
- `<feature>` — feature package: `^[a-z][a-z0-9]*$`, e.g. `history`.
- `<Screen>` — screen name in PascalCase, e.g. `HistoryDetail`.

## 1. App identity

| Item | Rule | Example |
|---|---|---|
| Folder | `apps/<app>/` | `apps/scanner/` |
| Gradle module | `:apps:<app>` | `:apps:scanner` |
| `namespace` | `io.github.romanfabio.voidapps.<app>` | `io.github.romanfabio.voidapps.scanner` |
| `applicationId` (release) | identical to `namespace` | `io.github.romanfabio.voidapps.scanner` |
| `applicationId` (debug) | `namespace` + `.debug` | `io.github.romanfabio.voidapps.scanner.debug` |
| Display name (`app_name`) | `Void <Display Name>` | `Void Scanner` |
| Display name, debug | `Void <Display Name> (debug)` | `Void Scanner (debug)` |
| Application class | `<Name>Application` | `ScannerApplication` |
| Root composable | `<Name>App` | `ScannerApp` |
| NavHost | `<Name>NavHost` | `ScannerNavHost` |
| Room database | `<Name>Database`, file `<app>.db` | `ScannerDatabase`, `scanner.db` |
| DataStore file | `<app>_preferences` | `scanner_preferences` |
| Release APK | `void-<app>-<versionName>.apk` | `void-scanner-1.2.0.apk` |

- The folder name, Gradle module name and last package segment are always identical.
  Single exception: the template lives in `apps/_template/` but its Gradle project is
  `:apps:template` and its package ends in `.template` (Gradle project names cannot start
  with `_`). The mapping is declared in `settings.gradle.kts`.
- `<app>` never contains `void`, hyphens, underscores, or Java/Kotlin keywords.
- `<Display Name>` may differ from `<Name>` (e.g. `<app>` = `qr`, display `Void QR Reader`)
  and is recorded in the app README.
- Once an app has been released, its `applicationId` never changes.

## 2. Core modules

| Item | Rule | Example |
|---|---|---|
| Folder | `core/<module>/`, `^[a-z][a-z0-9]*$` | `core/designsystem/` |
| Package / namespace | `io.github.romanfabio.voidapps.core.<module>` | `io.github.romanfabio.voidapps.core.designsystem` |

## 3. Packages

- All lowercase, no underscores: `feature.history.ui`, never `feature.historyDetail`.
- Layer packages are exactly `ui`, `domain`, `data`; sub-packages of `data` are exactly
  `model`, `local`, `di`.

## 4. Kotlin files and types

| Kind | Name | File |
|---|---|---|
| Stateful screen composable | `<Screen>Route` | `<Screen>Screen.kt` |
| Stateless screen composable | `<Screen>Screen` | `<Screen>Screen.kt` |
| ViewModel | `<Screen>ViewModel` | `<Screen>ViewModel.kt` |
| UI state | `<Screen>UiState` | `<Screen>UiState.kt` |
| User actions | `<Screen>Action` (sealed interface) | `<Screen>Action.kt` |
| One-off effects | `<Screen>Event` (sealed interface) | `<Screen>Action.kt` |
| Navigation destination | `<Screen>Destination` | `<Screen>Navigation.kt` |
| Nav graph extension | `NavGraphBuilder.<screen>Screen()` | `<Screen>Navigation.kt` |
| Navigate extension | `NavController.navigateTo<Screen>()` | `<Screen>Navigation.kt` |
| Use case | `<Verb><Noun>UseCase` | same as type |
| Repository interface | `<Noun>Repository` | same as type |
| Repository implementation | `Default<Noun>Repository` | same as type |
| Test fake | `Fake<Noun>Repository` | same as type |
| Domain/data model | `<Noun>` | same as type |
| Room entity | `<Noun>Entity` | same as type |
| Room DAO | `<Noun>Dao` | same as type |
| Hilt module | `<Purpose>Module` (`object` or `abstract class`) | same as type |
| Design-system component | `Void<Component>` | `Void<Component>.kt` |
| Preview function | `<Composable>Preview` (private) | next to the composable |
| Unit test class | `<ClassUnderTest>Test` | same as type |
| Instrumented/Compose test | `<ClassUnderTest>Test` | same as type |

- One top-level public class/interface per file; the file is named after it.
- Files holding only top-level functions/extensions: PascalCase noun describing the
  content, e.g. `FlowExtensions.kt`.

## 5. Code identifiers

| Kind | Rule | Example |
|---|---|---|
| Classes, interfaces, objects | PascalCase | `HistoryRepository` |
| Composable functions | PascalCase noun | `HistoryList` |
| Non-composable functions | camelCase, verb first | `loadHistory()` |
| Properties, variables, parameters | camelCase | `itemCount` |
| `const val` and top-level immutable constants | SCREAMING_SNAKE_CASE | `MAX_ITEMS` |
| Boolean properties | prefix `is`, `has`, `can`, `should` | `isLoading` |
| Backing `MutableStateFlow` | `_` + public name | `_uiState` |
| Action subtypes | past-tense or imperative verb phrase | `SearchQueryChanged`, `Refresh` |
| Event subtypes | `Navigate…`, `Show…` | `NavigateToDetail`, `ShowMessage` |
| Composable callbacks | `on<Event>` | `onItemClick` |
| `Modifier` parameter | `modifier: Modifier = Modifier`, first optional parameter | |

- Abbreviations are written as words: `Url`, `Id`, `Db`, `Qr` (`QrScanner`, not `QRScanner`).

## 6. Android resources

| Kind | Rule | Example |
|---|---|---|
| Layout files | none — XML layouts are forbidden | |
| Strings | `snake_case`, prefixed by feature | `history_empty_title` |
| Shared strings | prefix `common_` | `common_retry` |
| Content descriptions | suffix `_cd` | `history_delete_cd` |
| Drawables (icons) | `ic_<name>` | `ic_scan` |
| Drawables (illustrations) | `img_<name>` | `img_empty_history` |
| Launcher icon | `ic_launcher`, `ic_launcher_foreground`, `ic_launcher_background`, `ic_launcher_monochrome` | |

## 7. Git

### Branches
Format: `<type>/<scope>/<slug>`

- `<type>`: `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `chore`.
- `<scope>`: `<app>`, `core-<module>`, or `repo` (root, docs, CI).
- `<slug>`: kebab-case, max 40 chars.
- Examples: `feat/scanner/batch-export`, `fix/core-designsystem/dark-contrast`,
  `docs/repo/testing-rules`.
- The only long-lived branch is `main`.

### Commits — Conventional Commits 1.0
Format: `<type>(<scope>): <subject>`

- `<type>`: same list as branches.
- `<scope>`: same list as branches. Mandatory.
- `<subject>`: imperative mood, lowercase first letter, no trailing period, ≤ 72 chars
  for the whole header.
- Breaking changes (e.g. Room migration without data preservation, `applicationId`
  change): append `!` after the scope and add a `BREAKING CHANGE:` footer.
- Changes to protected paths (`CLAUDE.md` §6) state the approval in the body:
  `Approved-by: owner`.
- Examples:
  - `feat(scanner): add batch export to csv`
  - `fix(core-designsystem): raise contrast of outline color in dark theme`
  - `build(repo): bump compose bom`

### Commit identity
Every commit (author **and** committer) uses exactly:

```
Fabio Roman <61688500+romanfabio@users.noreply.github.com>
```

- Never use a personal or work email address; the GitHub noreply address keeps real
  addresses out of public history.
- The identity is set **per repository**, not globally, because the machine's global
  git config holds a different identity:

  ```bash
  git config --local user.name "Fabio Roman"
  git config --local user.email "61688500+romanfabio@users.noreply.github.com"
  ```

- Before the first commit in a fresh clone or worktree, verify it:
  `git config user.email` must print the noreply address. After committing,
  `git log -1 --format='%an <%ae> | %cn <%ce>'` must show it for both author and committer.
- Never override it with `-c user.email=…`, `--author`, or `GIT_AUTHOR_*`/`GIT_COMMITTER_*`
  variables, and never change the git config files of other scopes.
- If a commit was made with another identity, stop and ask the owner before rewriting
  history: rewriting a pushed `main` requires a force-push.

### Tags and releases
- Release tag: `<app>/v<major>.<minor>.<patch>`, e.g. `scanner/v1.2.0`.
- GitHub Release title: `Void <Display Name> <version>`, e.g. `Void Scanner 1.2.0`.

## 8. Versioning

- `versionName`: SemVer `MAJOR.MINOR.PATCH`, stored in `apps/<app>/version.properties` as
  `version=1.2.0`. No suffixes in release builds.
- `versionCode = MAJOR * 1_000_000 + MINOR * 1_000 + PATCH` (computed by
  `void.android.application`; never written by hand). Constraint: `MINOR` and `PATCH`
  ≤ 999.
- New apps start at `0.1.0`. `1.0.0` is the first version installed on a device other
  than the developer's.
