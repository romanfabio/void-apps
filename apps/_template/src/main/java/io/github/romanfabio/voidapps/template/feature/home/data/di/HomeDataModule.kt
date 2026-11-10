package io.github.romanfabio.voidapps.template.feature.home.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.romanfabio.voidapps.template.feature.home.data.DefaultItemRepository
import io.github.romanfabio.voidapps.template.feature.home.data.ItemRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataModule {
    @Binds
    abstract fun bindsItemRepository(repository: DefaultItemRepository): ItemRepository
}
