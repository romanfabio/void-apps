package io.github.romanfabio.voidapps.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Root theme of every Void app (docs/DESIGN_SYSTEM.md §2).
 * Dynamic color is intentionally not supported: the brand palette is fixed.
 */
@Composable
fun VoidTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalVoidSpacing provides VoidSpacing()) {
        MaterialTheme(
            colorScheme = if (darkTheme) VoidDarkColors else VoidLightColors,
            typography = VoidTypography,
            shapes = VoidShapes,
            content = content,
        )
    }
}

/** Accessors for Void-specific tokens that Material 3 does not model. */
object VoidTheme {
    val spacing: VoidSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalVoidSpacing.current
}
