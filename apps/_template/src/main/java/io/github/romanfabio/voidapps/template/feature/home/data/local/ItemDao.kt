package io.github.romanfabio.voidapps.template.feature.home.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    /** Newest first. */
    @Query("SELECT * FROM items ORDER BY id DESC")
    fun observeAll(): Flow<List<ItemEntity>>

    @Insert
    suspend fun insert(item: ItemEntity): Long

    /** Returns the number of deleted rows. */
    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
