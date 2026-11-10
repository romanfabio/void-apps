package io.github.romanfabio.voidapps.template.feature.home.ui

import io.github.romanfabio.voidapps.template.feature.home.data.model.Item

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(val items: List<Item>) : HomeUiState

    data object Error : HomeUiState
}
