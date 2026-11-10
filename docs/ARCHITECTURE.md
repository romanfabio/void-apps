# Architecture

Binding rules for how the repository and every app are structured.

## 1. Chosen architecture

**MVVM with unidirectional data flow (UDF), layered `ui` → `domain` → `data`**, following
Google's official *Guide to app architecture*.

Why: it is the architecture the Android toolchain (ViewModel, Lifecycle, Hilt,
Navigation Compose) is designed around, it maps directly onto Compose's state model, and
it keeps the per-app ceremony small for offline-first apps without a backend.

```
 ┌───────────── ui ─────────────┐     ┌─── domain (optional) ───┐     ┌──────── data ────────┐
 │ <X>Screen ◄── uiState ─┐     │     │                         │     │ <Noun>Repository     │
 │    │                   │     │     │ <Verb><Noun>UseCase     │     │   ├─ Room DAO        │
 │    └── onAction ──► <X>ViewModel ──►  (operator fun invoke)  ├────►│   └─ DataStore       │
 └──────────────────────────────┘     └─────────────────────────┘     └──────────────────────┘
          state flows up ▲                                     calls flow down ▼
```

- **State flows down** to composables as an immutable `UiState`.
- **Actions flow up** from composables to the ViewModel as a sealed `Action` type.
- The **data layer is the single source of truth**; the UI never caches data itself.

## 2. Gradle module layout

```
:core:designsystem   Android library, Compose  — theme, tokens, shared components
:core:common         Android library           — dispatchers, VoidResult, utilities
:core:licenses       Android library, Compose  — About & licenses screen
:core:testing        Android library           — test rules, fakes, helpers
:apps:template       Android application       — skeleton in folder apps/_template, kept compiling in CI
:apps:<app>          Android application       — one per app
```

Allowed module dependencies (anything not listed is forbidden):

| Module | May depend on |
|---|---|
| `:core:common` | nothing in this repo |
| `:core:designsystem` | `:core:common` |
| `:core:licenses` | `:core:designsystem`, `:core:common` |
| `:core:testing` | `:core:common` |
| `:apps:<app>` | any `:core:*` (`:core:testing` only as `testImplementation` / `androidTestImplementation`) |

- An app **never** depends on another app.
- A `core` module **never** depends on an app.
- Each app is a **single Gradle module**. When an app exceeds **8 packages under
  `feature/`**, splitting it into feature modules must be proposed to the owner (approval
  required, see `CLAUDE.md` §6).
- New `core` modules require approval. Code moves to `core` only when **two or more apps**
  need it.

## 3. Build configuration

- Every module applies **convention plugins** from `build-logic/convention`; module build
  files contain no repeated Android configuration.

| Plugin id | Applies to | Configures |
|---|---|---|
| `void.android.application` | `:apps:*` | AGP app, SDK levels, build types, signing, versioning, Java 21 toolchain |
| `void.android.library` | `:core:*` | AGP library, SDK levels (minSdk = core floor), Java 21 toolchain |
| `void.compose` | modules with UI | Compose compiler plugin, Compose BOM, tooling |
| `void.hilt` | modules using DI | Hilt plugin, KSP, Hilt dependencies |
| `void.quality` | every module | Spotless (ktlint), detekt, Kover |
| `void.licenses` | `:apps:*` | AboutLibraries plugin with the permissive-license allowlist |

- Annotation processing uses **KSP**. kapt is forbidden.
- An app's build file sets only: `namespace`, `applicationId` (equal to `namespace`),
  `minSdk` if it differs from the default, and its own dependencies.

## 4. Package structure inside an app

Root package: `io.github.romanfabio.voidapps.<app>` (see `NAMING.md`).

```
apps/<app>/src/main/java/io/github/romanfabio/voidapps/<app>/
├── <Name>Application.kt          @HiltAndroidApp
├── MainActivity.kt               @AndroidEntryPoint, setContent { VoidTheme { <Name>App() } }
├── <Name>App.kt                  root composable: adaptive scaffold + NavHost
├── navigation/
│   └── <Name>NavHost.kt          registers every feature's graph
├── di/                           app-wide Hilt modules (database, datastore)
├── data/                         app-wide data infrastructure
│   └── local/<Name>Database.kt   the single Room database of the app
└── feature/
    └── <feature>/
        ├── ui/
        │   ├── <Screen>Screen.kt       Route + Screen composables + previews
        │   ├── <Screen>ViewModel.kt
        │   ├── <Screen>UiState.kt
        │   ├── <Screen>Action.kt
        │   └── <Screen>Navigation.kt   Destination + NavGraphBuilder/NavController extensions
        ├── domain/                     optional, see §6
        │   └── <Verb><Noun>UseCase.kt
        └── data/
            ├── <Noun>Repository.kt         interface
            ├── Default<Noun>Repository.kt  implementation
            ├── model/<Noun>.kt             model exposed to domain/ui
            ├── local/<Noun>Dao.kt, <Noun>Entity.kt
            └── di/<Feature>DataModule.kt   @Binds repository
```

- Code lives in `src/main/java/` (Kotlin files in the `java` source set, the AGP default).
- Code shared by several features of the **same app** goes in
  `io.github.romanfabio.voidapps.<app>.common`.
- A feature never imports another feature's `ui` or `data` package. Cross-feature
  navigation goes through `navigation/`; shared data goes to `common`.

## 5. UI layer rules

1. One ViewModel per screen, annotated `@HiltViewModel`, injected with `hiltViewModel()`.
2. The ViewModel exposes exactly one `val uiState: StateFlow<<Screen>UiState>`, built with
   `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), <initial>)` or backed by
   a private `MutableStateFlow`.
3. The ViewModel receives input through exactly one `fun onAction(action: <Screen>Action)`.
   `<Screen>Action` is a `sealed interface`.
4. One-off effects (navigate, show snackbar) are exposed as
   `val events: Flow<<Screen>Event>` backed by `Channel(Channel.BUFFERED)` and collected
   in a `LaunchedEffect` inside the Route composable. Screens without effects omit it.
5. `<Screen>UiState`:
   - a `sealed interface` with `Loading`, `Success(...)`, `Error(...)` when the screen
     has mutually exclusive loading/content/error states;
   - otherwise a `data class` with only `val` properties and default values.
6. Every screen has two composables in `<Screen>Screen.kt`:
   - `<Screen>Route(...)` — stateful: obtains the ViewModel, collects state with
     `collectAsStateWithLifecycle()`, handles events. Not previewed, not unit-tested.
   - `<Screen>Screen(uiState, onAction, modifier)` — stateless, takes no ViewModel,
     has at least one `@PreviewLightDark` preview per UiState variant.
7. Composables never call repositories, use cases, DAOs, or `Context`-based I/O.
8. ViewModels never reference `android.content.Context`, `Activity`, or Compose types.

## 6. Domain layer rules

- A use case is **required** when logic is used by **two or more ViewModels** or
  combines **two or more repositories**. Otherwise the ViewModel calls the repository
  directly and the `domain/` package is omitted.
- One public method per use case: `operator fun invoke(...)`.
- Use cases have no Android dependencies (no `android.*` imports).

## 7. Data layer rules

1. A repository is an `interface` `<Noun>Repository` with one implementation
   `Default<Noun>Repository`, bound with `@Binds` in a Hilt module.
2. Observable data is exposed as `Flow<T>`; one-shot operations are `suspend` functions.
3. One-shot operations that can fail return `VoidResult<T>` from `:core:common`;
   exceptions never cross the repository boundary. `CancellationException` is always
   rethrown (use `runSuspendCatching` from `:core:common`).
4. Room entities (`<Noun>Entity`) and DAOs never leave the `data` package; repositories
   map them to `model/<Noun>`.
5. Each app has at most **one** Room database (`<Name>Database`) and at most **one**
   Preferences DataStore file (`<app>_preferences`).
6. Room schemas are exported to `apps/<app>/schemas/` and committed; every schema
   version bump ships with a `Migration` and a migration test.

## 8. Concurrency

- Dispatchers are injected with the `@Dispatcher(VoidDispatchers.IO)` /
  `@Dispatcher(VoidDispatchers.Default)` qualifiers from `:core:common`.
  Referencing `Dispatchers.IO` / `Dispatchers.Default` directly outside `:core:common`
  is forbidden (detekt `InjectDispatcher`).
- `GlobalScope`, `runBlocking` (outside tests) and `Thread` are forbidden.
- Long-running work started from the UI runs in `viewModelScope`; work that must survive
  the process uses WorkManager (requires approval as a new dependency).

## 9. Navigation

- Navigation Compose with type-safe routes. Each destination is a `@Serializable`
  `data object` or `data class` named `<Screen>Destination`.
- Each feature exposes `fun NavGraphBuilder.<screen>Screen(...)` and
  `fun NavController.navigateTo<Screen>(...)` in `<Screen>Navigation.kt`.
- `<Name>NavHost` is the only place that knows all destinations.
- The "About & licenses" destination from `:core:licenses` is registered in every app
  (see `DESIGN_SYSTEM.md` §8).

## 10. Adaptive layout

- `<Name>App` reads `currentWindowAdaptiveInfoV2()` once and passes layout decisions down.
- Top-level navigation uses `NavigationSuiteScaffold` (bottom bar on compact width,
  navigation rail on medium/expanded) when the app has 2–5 top-level destinations.
- See `DESIGN_SYSTEM.md` §7 for layout breakpoints and content width limits.
