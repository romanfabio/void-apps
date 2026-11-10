package io.github.romanfabio.voidapps.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme

const val VOID_LOADING_STATE_TAG = "VoidLoadingState"

/** Full-area loading indicator for `UiState.Loading` (docs/DESIGN_SYSTEM.md §8). */
@Composable
fun VoidLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().testTag(VOID_LOADING_STATE_TAG),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@PreviewLightDark
@Composable
private fun VoidLoadingStatePreview() {
    VoidTheme { VoidLoadingState() }
}
