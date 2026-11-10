package io.github.romanfabio.voidapps.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.romanfabio.voidapps.core.designsystem.R
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidSizes
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme

/**
 * Full-area error message for `UiState.Error` (docs/DESIGN_SYSTEM.md §8).
 * Shows a retry button when [onRetry] is not null.
 */
@Composable
fun VoidErrorState(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = modifier.fillMaxSize().padding(VoidTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(VoidTheme.spacing.lg, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            modifier = Modifier.size(VoidSizes.iconLarge),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            VoidSecondaryButton(text = stringResource(R.string.common_retry), onClick = onRetry)
        }
    }
}

@PreviewLightDark
@Composable
private fun VoidErrorStatePreview() {
    VoidTheme { VoidErrorState(message = "Something went wrong.", onRetry = {}) }
}
