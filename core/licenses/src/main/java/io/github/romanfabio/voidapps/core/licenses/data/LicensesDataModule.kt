package io.github.romanfabio.voidapps.core.licenses.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LicensesDataModule {
    @Binds
    abstract fun bindsAboutRepository(repository: DefaultAboutRepository): AboutRepository
}
