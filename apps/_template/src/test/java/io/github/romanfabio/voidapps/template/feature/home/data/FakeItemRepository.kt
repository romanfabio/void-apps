package io.github.romanfabio.voidapps.template.feature.home.data

import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeItemRepository : ItemRepository {
    val items = MutableStateFlow<List<Item>>(emptyList())
    var failObserve = false
    var failWrites = false

    override fun observeItems(): Flow<List<Item>> = if (failObserve) {
        flow { throw IllegalStateException("observe failed") }
    } else {
        items
    }

    override suspend fun addItem(name: String): VoidResult<Long> {
        if (failWrites) return VoidResult.Failure(IllegalStateException("add failed"))
        val id = (items.value.maxOfOrNull { it.id } ?: 0) + 1
        items.value = listOf(Item(id, name)) + items.value
        return VoidResult.Success(id)
    }

    override suspend fun deleteItem(id: Long): VoidResult<Unit> {
        if (failWrites) return VoidResult.Failure(IllegalStateException("delete failed"))
        items.value = items.value.filterNot { it.id == id }
        return VoidResult.Success(Unit)
    }
}
