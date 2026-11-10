package io.github.romanfabio.voidapps.core.licenses.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import io.github.romanfabio.voidapps.core.designsystem.component.VoidErrorState
import io.github.romanfabio.voidapps.core.designsystem.component.VoidLoadingState
import io.github.romanfabio.voidapps.core.designsystem.component.VoidTopAppBar
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidSizes
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme
import io.github.romanfabio.voidapps.core.licenses.R

@Composable
internal fun AboutLicensesRoute(onNavigateUp: () -> Unit, viewModel: AboutLicensesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AboutLicensesScreen(uiState = uiState, onAction = viewModel::onAction, onNavigateUp = onNavigateUp)
}

@Composable
internal fun AboutLicensesScreen(
    uiState: AboutLicensesUiState,
    onAction: (AboutLicensesAction) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { VoidTopAppBar(title = stringResource(R.string.licenses_title), onNavigateUp = onNavigateUp) },
    ) { padding ->
        val contentModifier = Modifier.fillMaxSize().padding(padding)
        when (uiState) {
            AboutLicensesUiState.Loading -> VoidLoadingState(contentModifier)

            AboutLicensesUiState.Error -> VoidErrorState(
                message = stringResource(R.string.licenses_load_error),
                modifier = contentModifier,
                onRetry = { onAction(AboutLicensesAction.Retry) },
            )

            is AboutLicensesUiState.Success -> LibrariesContainer(
                libraries = uiState.libraries,
                modifier = contentModifier,
                contentPadding = PaddingValues(bottom = VoidTheme.spacing.lg),
                header = { aboutHeader(uiState) },
            )
        }
    }
}

private fun LazyListScope.aboutHeader(state: AboutLicensesUiState.Success) {
    item(key = "app") {
        AppSection(state.appName, state.versionName, state.versionCode)
    }
    item(key = "license") {
        LicenseSection(state.licenseText)
    }
    item(key = "libraries_title") {
        Text(
            text = stringResource(R.string.licenses_libraries_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = VoidTheme.spacing.lg, vertical = VoidTheme.spacing.md),
        )
    }
}

@Composable
private fun AppSection(appName: String, versionName: String, versionCode: Long) {
    val uriHandler = LocalUriHandler.current
    val sourceUrl = stringResource(R.string.licenses_source_url)
    Column(
        modifier = Modifier.fillMaxWidth().padding(VoidTheme.spacing.lg),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = appName, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = stringResource(R.string.licenses_version, versionName, versionCode),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = { uriHandler.openUri(sourceUrl) }) {
            Text(text = stringResource(R.string.licenses_source_code))
        }
    }
}

@Composable
private fun LicenseSection(licenseText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = VoidSizes.contentMaxWidth)
            .padding(horizontal = VoidTheme.spacing.lg),
    ) {
        Column(modifier = Modifier.padding(VoidTheme.spacing.lg)) {
            Text(
                text = stringResource(R.string.licenses_app_license_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = licenseText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = VoidTheme.spacing.sm),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AboutLicensesScreenSuccessPreview() {
    VoidTheme {
        AboutLicensesScreen(
            uiState = AboutLicensesUiState.Success(
                appName = "Void Template",
                versionName = "0.1.0",
                versionCode = 1_000,
                licenseText = "MIT License\n\nCopyright (c) 2026 romanfabio",
                libraries = Libs(libraries = emptyList(), licenses = emptySet()),
            ),
            onAction = {},
            onNavigateUp = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun AboutLicensesScreenErrorPreview() {
    VoidTheme {
        AboutLicensesScreen(uiState = AboutLicensesUiState.Error, onAction = {}, onNavigateUp = {})
    }
}
