package io.github.romanfabio.voidapps.template.feature.home.data

import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemDao
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeItemDao : ItemDao {
    private val entities = MutableStateFlow<List<ItemEntity>>(emptyList())
    var failInsert = false

    override fun observeAll(): Flow<List<ItemEntity>> = entities.map { list -> list.sortedByDescending { it.id } }

    override suspend fun insert(item: ItemEntity): Long {
        check(!failInsert) { "insert failed" }
        val id = (entities.value.maxOfOrNull { it.id } ?: 0) + 1
        entities.value = entities.value + item.copy(id = id)
        return id
    }

    override suspend fun deleteById(id: Long): Int {
        val before = entities.value.size
        entities.value = entities.value.filterNot { it.id == id }
        return before - entities.value.size
    }
}
