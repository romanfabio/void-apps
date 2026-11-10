---
name: add-screen
description: Add a new screen (Route, Screen, ViewModel, UiState, Action, Destination, tests) to an existing Void app following docs/ARCHITECTURE.md and docs/NAMING.md. Use when the owner asks for a new screen, page, or feature UI in apps/<app>.
---

# add-screen

Reference implementation to copy from: `apps/_template/src/main/java/.../feature/home/`
and its tests in `apps/_template/src/test/java/.../feature/home/`.

## 1. Collect inputs (ask if missing)

| Input | Rule | Example |
|---|---|---|
| `<app>` | Existing folder in `apps/` | `scanner` |
| `<feature>` | Package, `^[a-z][a-z0-9]*$` | `history` |
| `<Screen>` | PascalCase | `HistoryDetail` |
| Data needed | Which repository/data; new Room entity? | `ScanRepository` |
| Entry point | From which screen/action it opens | tap on a history row |
| Top-level? | Appears in bottom bar/rail? (max 5 top-level) | no |

If the screen needs a new permission, network access, or a new dependency, stop and ask
(CLAUDE.md §6).

## 2. Create files

Base path: `apps/<app>/src/main/java/io/github/romanfabio/voidapps/<app>/feature/<feature>/`

| File | Contents |
|---|---|
| `ui/<Screen>UiState.kt` | `sealed interface` Loading/Success/Error if the screen loads data, else `data class` with defaults |
| `ui/<Screen>Action.kt` | `sealed interface <Screen>Action`; `sealed interface <Screen>Event` if it has one-off effects |
| `ui/<Screen>ViewModel.kt` | `@HiltViewModel`; one `uiState: StateFlow` (`stateIn(..., WhileSubscribed(5_000), …)`); one `onAction()`; `events` via `Channel(BUFFERED).receiveAsFlow()` |
| `ui/<Screen>Screen.kt` | `internal fun <Screen>Route(...)` (hiltViewModel, collectAsStateWithLifecycle, events in LaunchedEffect) + `internal fun <Screen>Screen(uiState, onAction, …, modifier)` + `@PreviewLightDark` preview per UiState variant + one `@PreviewFontScale` |
| `ui/<Screen>Navigation.kt` | `@Serializable` `<Screen>Destination`; `NavGraphBuilder.<screen>Screen(...)`; `NavController.navigateTo<Screen>(...)` |
| `data/…` | Only if new data: `<Noun>Repository` + `Default<Noun>Repository` + `di/<Feature>DataModule` (`@Binds`); Room `<Noun>Entity`/`<Noun>Dao` in `data/local/` |
| `domain/<Verb><Noun>UseCase.kt` | Only if the logic is used by ≥ 2 ViewModels or combines ≥ 2 repositories |

Rules to check while writing:
- Use `VoidTopAppBar`, `VoidPrimaryButton`, `VoidSecondaryButton`, `VoidLoadingState`,
  `VoidErrorState`, `VoidEmptyState`; no colors, `sp`, or `dp` literals for spacing
  (`VoidTheme.spacing`, `VoidSizes`).
- Every user-visible string goes in `res/values/strings.xml` with prefix `<feature>_`;
  content descriptions end with `_cd`.
- Icons: vector drawables `ic_<name>.xml` (Material Icons/Symbols Outlined).
- ViewModel takes no `Context`; repositories return `VoidResult` for one-shot operations.

## 3. Wire it

1. Register `<screen>Screen(...)` in `navigation/<Name>NavHost.kt`.
2. Call `navigateTo<Screen>(...)` from the entry point via a callback parameter, never by
   passing `NavController` into screens.
3. New Room entity: add to `<Name>Database`, bump `version`, add a `Migration` and a
   migration test in `src/androidTest/` (docs/ARCHITECTURE.md §7). Not needed while the
   app is unreleased and the version is still 1 — then edit the entity in place.

## 4. Tests (docs/TESTING.md §3)

In `apps/<app>/src/test/java/.../feature/<feature>/`:
- `ui/<Screen>ViewModelTest.kt` — `MainDispatcherRule`, Turbine; initial state, every
  Action, every Event, error path.
- `ui/<Screen>ScreenTest.kt` — `@RunWith(RobolectricTestRunner::class)`,
  `androidx.compose.ui.test.junit4.v2.createComposeRule`; each UiState renders; each
  clickable element sends the right Action.
- `data/Fake<Noun>Repository.kt` and `data/Default<Noun>RepositoryTest.kt` if new data.
- `data/local/<Noun>DaoTest.kt` (Robolectric, in-memory Room) for every non-trivial query.

## 5. Finish

1. Update the "Screens" (and, if relevant, "Data and privacy") table in the app README.
2. Run the `verify` skill for `:apps:<app>`.
3. Commit on `feat/<app>/<slug>`: `feat(<app>): add <screen> screen`.
