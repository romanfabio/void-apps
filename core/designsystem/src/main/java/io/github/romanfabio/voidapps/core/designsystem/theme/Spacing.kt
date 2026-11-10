package io.github.romanfabio.voidapps.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale (docs/DESIGN_SYSTEM.md §5). Read it through `VoidTheme.spacing`. */
@Immutable
data class VoidSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
)

/** Fixed component sizes (docs/DESIGN_SYSTEM.md §5). */
object VoidSizes {
    val minTouchTarget: Dp = 48.dp
    val iconSmall: Dp = 18.dp
    val icon: Dp = 24.dp
    val iconLarge: Dp = 48.dp
    val contentMaxWidth: Dp = 600.dp
}

internal val LocalVoidSpacing = staticCompositionLocalOf { VoidSpacing() }
