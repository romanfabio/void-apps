package io.github.romanfabio.voidapps.template.feature.home.data

import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultItemRepositoryTest {

    private val dao = FakeItemDao()
    private val repository = DefaultItemRepository(dao, UnconfinedTestDispatcher())

    @Test
    fun `added items are observed as models newest first`() = runTest {
        repository.addItem("First")
        repository.addItem("Second")

        assertEquals(listOf(Item(2, "Second"), Item(1, "First")), repository.observeItems().first())
    }

    @Test
    fun `add returns the new id`() = runTest {
        assertEquals(VoidResult.Success(1L), repository.addItem("First"))
    }

    @Test
    fun `add wraps dao exceptions in failure`() = runTest {
        dao.failInsert = true

        assertIs<VoidResult.Failure>(repository.addItem("First"))
    }

    @Test
    fun `delete removes an existing item`() = runTest {
        repository.addItem("First")

        assertEquals(VoidResult.Success(Unit), repository.deleteItem(1))
        assertEquals(emptyList(), repository.observeItems().first())
    }

    @Test
    fun `delete of a missing item is a failure`() = runTest {
        val result = repository.deleteItem(99)

        val failure = assertIs<VoidResult.Failure>(result)
        assertIs<NoSuchElementException>(failure.error)
    }
}
