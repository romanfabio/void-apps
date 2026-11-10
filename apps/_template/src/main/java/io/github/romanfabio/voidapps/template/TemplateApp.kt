package io.github.romanfabio.voidapps.template

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme
import io.github.romanfabio.voidapps.template.navigation.TemplateNavHost

/**
 * Root composable. Reads the window size class once and passes layout decisions down
 * (docs/ARCHITECTURE.md §10). One top-level destination, so no NavigationSuiteScaffold.
 */
@Composable
fun TemplateApp(modifier: Modifier = Modifier) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isAtLeastMedium = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val horizontalPadding = if (isAtLeastMedium) VoidTheme.spacing.xl else VoidTheme.spacing.lg
    TemplateNavHost(horizontalPadding = horizontalPadding, modifier = modifier)
}
