package com.example.merchtools.di

import com.example.merchtools.data.local.repository.OfflineStoreRepository
import com.example.merchtools.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindStoreRepository(repository: OfflineStoreRepository): StoreRepository

}

/** TODO
 *  Finish implementing data modules with the rest of the repositories **/