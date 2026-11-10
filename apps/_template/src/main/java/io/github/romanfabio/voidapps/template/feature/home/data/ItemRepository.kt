package io.github.romanfabio.voidapps.template.feature.home.data

import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun observeItems(): Flow<List<Item>>

    /** Returns the id of the new item. */
    suspend fun addItem(name: String): VoidResult<Long>

    suspend fun deleteItem(id: Long): VoidResult<Unit>
}
