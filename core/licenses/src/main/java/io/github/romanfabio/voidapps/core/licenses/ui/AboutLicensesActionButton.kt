package io.github.romanfabio.voidapps.core.licenses.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.romanfabio.voidapps.core.licenses.R

/** Top-app-bar action that opens About & licenses (docs/DESIGN_SYSTEM.md §8). */
@Composable
fun AboutLicensesActionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = stringResource(R.string.licenses_open_cd),
        )
    }
}
