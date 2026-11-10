package io.github.romanfabio.voidapps.template.feature.home.ui

import app.cash.turbine.test
import io.github.romanfabio.voidapps.core.testing.MainDispatcherRule
import io.github.romanfabio.voidapps.template.R
import io.github.romanfabio.voidapps.template.feature.home.data.FakeItemRepository
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeItemRepository()

    @Test
    fun `initial state is loading`() {
        assertEquals(HomeUiState.Loading, HomeViewModel(repository).uiState.value)
    }

    @Test
    fun `items from the repository are exposed as success`() = runTest {
        repository.items.value = listOf(Item(1, "First"))
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Success(listOf(Item(1, "First"))), awaitItem())
        }
    }

    @Test
    fun `observe failure exposes error`() = runTest {
        repository.failObserve = true
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Error, awaitItem())
        }
    }

    @Test
    fun `add item action adds the item`() = runTest {
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Success(emptyList()), awaitItem())
            viewModel.onAction(HomeAction.AddItem("Item 1"))
            assertEquals(HomeUiState.Success(listOf(Item(1, "Item 1"))), awaitItem())
        }
    }

    @Test
    fun `add failure emits an error message`() = runTest {
        repository.failWrites = true
        val viewModel = HomeViewModel(repository)

        viewModel.events.test {
            viewModel.onAction(HomeAction.AddItem("Item 1"))
            assertEquals(HomeEvent.ShowMessage(R.string.home_add_error), awaitItem())
        }
    }

    @Test
    fun `delete emits a confirmation message`() = runTest {
        repository.items.value = listOf(Item(1, "First"))
        val viewModel = HomeViewModel(repository)

        viewModel.events.test {
            viewModel.onAction(HomeAction.DeleteItem(1))
            assertEquals(HomeEvent.ShowMessage(R.string.home_item_deleted), awaitItem())
        }
        assertEquals(emptyList(), repository.items.value)
    }

    @Test
    fun `delete failure emits an error message`() = runTest {
        repository.failWrites = true
        val viewModel = HomeViewModel(repository)

        viewModel.events.test {
            viewModel.onAction(HomeAction.DeleteItem(1))
            assertEquals(HomeEvent.ShowMessage(R.string.home_delete_error), awaitItem())
        }
    }
}
