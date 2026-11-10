package io.github.romanfabio.voidapps.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.romanfabio.voidapps.core.designsystem.R
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme

/**
 * Top app bar used by every screen with a title (docs/DESIGN_SYSTEM.md §8).
 * Shows an "up" arrow when [onNavigateUp] is not null.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoidTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = modifier,
        navigationIcon = {
            if (onNavigateUp != null) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.common_navigate_up_cd),
                    )
                }
            }
        },
        actions = actions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun VoidTopAppBarPreview() {
    VoidTheme {
        VoidTopAppBar(title = "Void Template", onNavigateUp = {})
    }
}
