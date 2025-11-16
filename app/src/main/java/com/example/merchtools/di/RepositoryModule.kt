package com.example.merchtools.di

import com.example.merchtools.data.local.repository.OfflineStoreRepository
import com.example.merchtools.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStoreRepository(
        offlineStoreRepository: OfflineStoreRepository
    ): StoreRepository
}