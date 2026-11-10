package io.github.romanfabio.voidapps.template.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.romanfabio.voidapps.core.designsystem.component.VoidEmptyState
import io.github.romanfabio.voidapps.core.designsystem.component.VoidErrorState
import io.github.romanfabio.voidapps.core.designsystem.component.VoidLoadingState
import io.github.romanfabio.voidapps.core.designsystem.component.VoidTopAppBar
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidSizes
import io.github.romanfabio.voidapps.core.designsystem.theme.VoidTheme
import io.github.romanfabio.voidapps.core.licenses.ui.AboutLicensesActionButton
import io.github.romanfabio.voidapps.template.R
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item

@Composable
internal fun HomeRoute(horizontalPadding: Dp, onOpenAbout: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ShowMessage -> snackbarHostState.showSnackbar(resources.getString(event.messageRes))
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onOpenAbout = onOpenAbout,
        horizontalPadding = horizontalPadding,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onOpenAbout: () -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val itemCount = (uiState as? HomeUiState.Success)?.items?.size ?: 0
    val newItemName = stringResource(R.string.home_new_item_name, itemCount + 1)
    Scaffold(
        modifier = modifier,
        topBar = {
            VoidTopAppBar(
                title = stringResource(R.string.home_title),
                actions = { AboutLicensesActionButton(onClick = onOpenAbout) },
            )
        },
        floatingActionButton = {
            if (uiState is HomeUiState.Success) {
                FloatingActionButton(onClick = { onAction(HomeAction.AddItem(newItemName)) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.home_add_cd),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val contentModifier = Modifier.fillMaxSize().padding(padding)
        when (uiState) {
            HomeUiState.Loading -> VoidLoadingState(contentModifier)

            HomeUiState.Error -> VoidErrorState(
                message = stringResource(R.string.home_load_error),
                modifier = contentModifier,
            )

            is HomeUiState.Success -> if (uiState.items.isEmpty()) {
                VoidEmptyState(
                    title = stringResource(R.string.home_empty_title),
                    body = stringResource(R.string.home_empty_body),
                    modifier = contentModifier,
                )
            } else {
                ItemList(
                    items = uiState.items,
                    onDelete = { onAction(HomeAction.DeleteItem(it)) },
                    horizontalPadding = horizontalPadding,
                    modifier = contentModifier,
                )
            }
        }
    }
}

@Composable
private fun ItemList(
    items: List<Item>,
    onDelete: (Long) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().widthIn(max = VoidSizes.contentMaxWidth),
            contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = VoidTheme.spacing.sm),
        ) {
            items(items, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                    trailingContent = {
                        IconButton(onClick = { onDelete(item.id) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.home_delete_cd, item.name),
                            )
                        }
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun HomeScreenSuccessPreview() {
    VoidTheme {
        HomeScreen(
            uiState = HomeUiState.Success(listOf(Item(2, "Item 2"), Item(1, "Item 1"))),
            onAction = {},
            onOpenAbout = {},
            horizontalPadding = VoidTheme.spacing.lg,
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenEmptyPreview() {
    VoidTheme {
        HomeScreen(
            uiState = HomeUiState.Success(emptyList()),
            onAction = {},
            onOpenAbout = {},
            horizontalPadding = VoidTheme.spacing.lg,
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenLoadingPreview() {
    VoidTheme {
        HomeScreen(uiState = HomeUiState.Loading, onAction = {
        }, onOpenAbout = {}, horizontalPadding = VoidTheme.spacing.lg)
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenErrorPreview() {
    VoidTheme {
        HomeScreen(uiState = HomeUiState.Error, onAction = {
        }, onOpenAbout = {}, horizontalPadding = VoidTheme.spacing.lg)
    }
}
