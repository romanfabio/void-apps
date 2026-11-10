package io.github.romanfabio.voidapps.template.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.romanfabio.voidapps.template.data.local.TemplateDatabase
import io.github.romanfabio.voidapps.template.feature.home.data.local.ItemDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): TemplateDatabase =
        Room.databaseBuilder(context, TemplateDatabase::class.java, TemplateDatabase.NAME).build()

    @Provides
    fun providesItemDao(database: TemplateDatabase): ItemDao = database.itemDao()
}
