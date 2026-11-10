package io.github.romanfabio.voidapps.template.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.template.R
import io.github.romanfabio.voidapps.template.feature.home.data.ItemRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.observeItems()
        .map<_, HomeUiState> { HomeUiState.Success(it) }
        .catch { emit(HomeUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), HomeUiState.Loading)

    private val eventChannel = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = eventChannel.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.AddItem -> addItem(action.name)
            is HomeAction.DeleteItem -> deleteItem(action.id)
        }
    }

    private fun addItem(name: String) {
        viewModelScope.launch {
            if (repository.addItem(name) is VoidResult.Failure) {
                eventChannel.send(HomeEvent.ShowMessage(R.string.home_add_error))
            }
        }
    }

    private fun deleteItem(id: Long) {
        viewModelScope.launch {
            val message = when (repository.deleteItem(id)) {
                is VoidResult.Success -> R.string.home_item_deleted
                is VoidResult.Failure -> R.string.home_delete_error
            }
            eventChannel.send(HomeEvent.ShowMessage(message))
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
