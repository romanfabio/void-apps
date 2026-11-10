package io.github.romanfabio.voidapps.template.feature.home.data

import io.github.romanfabio.voidapps.core.common.dispatcher.Dispatcher
import io.github.romanfabio.voidapps.core.common.dispatcher.VoidDispatchers
import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.core.common.result.runSuspendCatching
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemDao
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemEntity
import io.github.romanfabio.voidapps.template.feature.home.data.model.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    @param:Dispatcher(VoidDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ItemRepository {

    override fun observeItems(): Flow<List<Item>> = itemDao.observeAll().map { entities ->
        entities.map { it.toModel() }
    }

    override suspend fun addItem(name: String): VoidResult<Long> = runSuspendCatching {
        withContext(ioDispatcher) { itemDao.insert(ItemEntity(name = name)) }
    }

    override suspend fun deleteItem(id: Long): VoidResult<Unit> = runSuspendCatching {
        val deleted = withContext(ioDispatcher) { itemDao.deleteById(id) }
        if (deleted == 0) throw NoSuchElementException("No item with id $id")
    }

    private fun ItemEntity.toModel() = Item(id = id, name = name)
}
