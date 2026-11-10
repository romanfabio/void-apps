package io.github.romanfabio.voidapps.core.designsystem.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidSizes
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme

/** The single main action of a screen (docs/DESIGN_SYSTEM.md §8). */
@Composable
fun VoidPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = VoidSizes.minTouchTarget),
        enabled = enabled,
    ) {
        Text(text = text)
    }
}

@PreviewLightDark
@Composable
private fun VoidPrimaryButtonPreview() {
    VoidTheme { VoidPrimaryButton(text = "Save", onClick = {}) }
}
