# Design system

All tokens and shared components live in `:core:designsystem`
(`io.github.romanfabio.voidapps.core.designsystem`). Apps consume them; apps never define
their own colors, type styles, spacing values, or shapes.

## 1. Principles

1. **Material 3** is the component foundation. Void tokens are injected through
   `MaterialTheme` so stock M3 components are on-brand by default.
2. **Same look in every app.** Brand identity comes from the fixed palette, so
   **dynamic color is disabled**.
3. **Dark and light are both first-class.** The theme follows the system setting;
   every screen is checked in both (`@PreviewLightDark`).
4. **Edge-to-edge** on every screen (`enableEdgeToEdge()` in `MainActivity`).

## 2. Theme entry point

```kotlin
VoidTheme(darkTheme: Boolean = isSystemInDarkTheme()) { content() }
```

- `MainActivity.setContent` wraps the whole app in `VoidTheme`. Every `@Preview` wraps
  its content in `VoidTheme`.
- The manifest `<application android:theme>` is always `@style/Theme.Void` from
  `:core:designsystem` (window background = brand surface, so there is no white flash at
  launch in dark mode). Apps define no XML themes or colors of their own; the only
  exception is the launcher-icon background color.
- Tokens are read through `MaterialTheme.colorScheme`, `MaterialTheme.typography`,
  `MaterialTheme.shapes`, and `VoidTheme.spacing`.
- Forbidden in app modules: `Color(0x…)`, `Color.Red`-style constants, `.sp` literals,
  `TextStyle(...)` constructors, `RoundedCornerShape(...)` literals, and `.dp` literals for
  padding/spacing (use `VoidTheme.spacing`). Size values for icons and touch targets use
  the constants in `VoidSizes`.

## 3. Color

Palette names: **Void indigo** (primary), **Nebula teal** (secondary),
**Flare amber** (tertiary), **Void black** (dark background).

### Light scheme (`VoidLightColors`)

| Role | Hex | Role | Hex |
|---|---|---|---|
| primary | `#4B3FD9` | onPrimary | `#FFFFFF` |
| primaryContainer | `#E3DFFF` | onPrimaryContainer | `#150A6B` |
| secondary | `#00696B` | onSecondary | `#FFFFFF` |
| secondaryContainer | `#9CF1F2` | onSecondaryContainer | `#002021` |
| tertiary | `#845400` | onTertiary | `#FFFFFF` |
| tertiaryContainer | `#FFDDB3` | onTertiaryContainer | `#2A1800` |
| error | `#BA1A1A` | onError | `#FFFFFF` |
| errorContainer | `#FFDAD6` | onErrorContainer | `#410002` |
| background / surface | `#FCF8FF` | onBackground / onSurface | `#1B1B21` |
| surfaceVariant | `#E4E1EC` | onSurfaceVariant | `#47464F` |
| outline | `#777680` | outlineVariant | `#C8C5D0` |
| surfaceContainerLowest | `#FFFFFF` | surfaceContainerLow | `#F6F2FA` |
| surfaceContainer | `#F0ECF6` | surfaceContainerHigh | `#EAE7F0` |
| surfaceContainerHighest | `#E4E1EA` | surfaceDim / surfaceBright | `#DCD8E0` / `#FCF8FF` |
| inverseSurface | `#303036` | inverseOnSurface | `#F3EFF7` |
| inversePrimary | `#C5C0FF` | scrim | `#000000` |

### Dark scheme (`VoidDarkColors`)

| Role | Hex | Role | Hex |
|---|---|---|---|
| primary | `#C5C0FF` | onPrimary | `#24168F` |
| primaryContainer | `#3528B8` | onPrimaryContainer | `#E3DFFF` |
| secondary | `#6FD6D7` | onSecondary | `#003738` |
| secondaryContainer | `#004F51` | onSecondaryContainer | `#9CF1F2` |
| tertiary | `#FFB952` | onTertiary | `#462A00` |
| tertiaryContainer | `#643F00` | onTertiaryContainer | `#FFDDB3` |
| error | `#FFB4AB` | onError | `#690005` |
| errorContainer | `#93000A` | onErrorContainer | `#FFDAD6` |
| background / surface | `#0B0B10` | onBackground / onSurface | `#E5E1EA` |
| surfaceVariant | `#47464F` | onSurfaceVariant | `#C8C5D0` |
| outline | `#918F9A` | outlineVariant | `#47464F` |
| surfaceContainerLowest | `#07070B` | surfaceContainerLow | `#16161C` |
| surfaceContainer | `#1E1E26` | surfaceContainerHigh | `#28282F` |
| surfaceContainerHighest | `#33333A` | surfaceDim / surfaceBright | `#0B0B10` / `#37363E` |
| inverseSurface | `#E5E1EA` | inverseOnSurface | `#303036` |
| inversePrimary | `#4B3FD9` | scrim | `#000000` |

### Color usage rules
- Text and icons use the `on*` role matching their container (e.g. text on
  `primaryContainer` uses `onPrimaryContainer`).
- `primary` is for the single most important action per screen; `secondary` for
  selection/filters; `tertiary` for highlights and badges; `error` only for errors and
  destructive actions.
- Elevation is expressed with `surfaceContainer*` roles (tonal elevation), never with
  custom shadows.
- Any new color role requires approval and must reach the contrast ratios in §9.

## 4. Typography

- `VoidTypography` = the Material 3 default type scale (`displayLarge` … `labelSmall`)
  with `FontFamily.Default` (Roboto). ⚠️ DECISION NEEDED: brand font. If chosen it must be
  under the SIL Open Font License or Apache-2.0, bundled in
  `core/designsystem/src/main/res/font/` (no downloadable fonts, which depend on Google
  Play services), and replaces only the `fontFamily` of each style.
- Mapping:

| Use | Style |
|---|---|
| Screen title in top app bar | `titleLarge` |
| Section header | `titleMedium` |
| List item primary text | `bodyLarge` |
| List item secondary text | `bodyMedium` with `onSurfaceVariant` |
| Body paragraphs | `bodyLarge` |
| Buttons, chips, tabs | `labelLarge` (applied by M3 components) |
| Captions, metadata | `labelMedium` |
| Empty/error state title | `headlineSmall` |
| Hero numbers / big values | `displaySmall` |

- Text never sets `fontSize` or `fontWeight` directly; pick a style.

## 5. Spacing and sizes

`VoidTheme.spacing` (`VoidSpacing`):

| Token | Value | Token | Value |
|---|---|---|---|
| `none` | 0dp | `lg` | 16dp |
| `xxs` | 2dp | `xl` | 24dp |
| `xs` | 4dp | `xxl` | 32dp |
| `sm` | 8dp | `xxxl` | 48dp |
| `md` | 12dp | | |

Rules:
- Screen horizontal padding: `lg` (16dp) on compact width, `xl` (24dp) on medium and
  expanded width.
- Vertical gap between list items: `sm`; between sections: `xl`.
- Padding inside cards: `lg`.

`VoidSizes`: `minTouchTarget = 48.dp`, `iconSmall = 18.dp`, `icon = 24.dp`,
`iconLarge = 48.dp`, `contentMaxWidth = 600.dp`.

## 6. Shapes

`VoidShapes` = Material 3 defaults: `extraSmall` 4dp, `small` 8dp, `medium` 12dp,
`large` 16dp, `extraLarge` 28dp. Cards use `medium`; bottom sheets and dialogs use the
M3 default (`extraLarge`).

## 7. Layout

- Window size classes come from `currentWindowAdaptiveInfoV2().windowSizeClass`:
  compact `< 600dp`, medium `600–839dp`, expanded `≥ 840dp` width.
- Top-level navigation: `NavigationSuiteScaffold` (bottom bar on compact, rail on medium
  and expanded) when the app has 2–5 top-level destinations; a single-destination app
  uses a plain `Scaffold` with `VoidTopAppBar`.
- Reading content (text, forms, settings) is limited to `VoidSizes.contentMaxWidth`
  (600dp) and centered on medium/expanded widths.
- List/detail flows use a two-pane layout on expanded width.
- Every screen is usable in portrait and landscape; orientation is never locked.
- Insets: content respects system bars via `Scaffold` `contentPadding` / `WindowInsets`;
  no content is drawn under the status or navigation bar except backgrounds.

## 8. Shared components

Provided by `:core:designsystem` (names start with `Void`):

| Component | Wraps | Use |
|---|---|---|
| `VoidTopAppBar` | M3 `TopAppBar` | Every screen with a title |
| `VoidPrimaryButton` | M3 `Button` | Main action of a screen (max one per screen) |
| `VoidSecondaryButton` | M3 `OutlinedButton` | Secondary actions |
| `VoidLoadingState` | `CircularProgressIndicator` centered | `UiState.Loading` |
| `VoidErrorState` | icon + text + retry button | `UiState.Error` |
| `VoidEmptyState` | icon + title + body + optional action | empty lists |

Rules:
- When a `Void*` wrapper exists, apps use it and not the wrapped M3 component directly.
- Other M3 components (`Card`, `ListItem`, `Switch`, `Checkbox`, `AlertDialog`,
  `ModalBottomSheet`, `SnackbarHost`, …) are used directly with theme defaults; passing
  custom `colors =` is forbidden.
- A component goes into `:core:designsystem` when **two or more apps** need it
  (approval required). Single-app components stay in the app's `common/ui` package.
- Icons: Google Material Icons **Outlined** (or Material Symbols Outlined, weight 400, grade 0,
  optical size 24), 24dp, imported as
  vector drawables (`ic_<name>.xml`, Apache-2.0). `material-icons-extended` is forbidden
  (APK size).

### About & licenses (mandatory in every app)
- Provided by `:core:licenses` as `aboutLicensesScreen()` / `navigateToAboutLicenses()`.
- Reachable from the start destination in **≤ 2 taps**: an `ic_info` action in the start
  screen's `VoidTopAppBar`, or an "About & licenses" item on a Settings screen.
- Shows: app name, `versionName (versionCode)`, the full MIT license text, a link to
  `https://github.com/romanfabio/void-apps`, and the list of all runtime dependencies with
  their licenses (AboutLibraries data generated at build time).

## 9. Accessibility (mandatory)

1. Touch targets ≥ 48×48dp (`VoidSizes.minTouchTarget`).
2. Contrast: text ≥ 4.5:1 (≥ 3:1 for text ≥ 18sp regular or 14sp bold); icons and
   component boundaries ≥ 3:1.
3. Every non-decorative `Icon`/`Image` has a `contentDescription` from string resources
   (`*_cd`); decorative ones pass `null`.
4. Layouts work at font scale 2.0 without clipped or overlapping text; previews include
   `@PreviewFontScale` for each screen's main state.
5. Information is never conveyed by color alone.
6. All interactive elements are reachable and operable with TalkBack and with a
   keyboard/D-pad.

## 10. Launcher icon

- Adaptive icon with foreground, background and **monochrome** layers (themed icons).
- Background layer: solid Void black `#0B0B10`. Foreground glyph: Void indigo dark
  variant `#C5C0FF`.
- ⚠️ DECISION NEEDED: icon artwork for each app and the shared visual motif. The template
  ships a placeholder.
