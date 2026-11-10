package io.github.romanfabio.voidapps.template.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemDao
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemEntity

/** The single Room database of this app (docs/ARCHITECTURE.md §7). Schemas: apps/_template/schemas/. */
@Database(entities = [ItemEntity::class], version = 1, exportSchema = true)
abstract class TemplateDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        const val NAME = "template.db"
    }
}
