package com.example.merchtools.di

import com.example.merchtools.data.local.repository.OfflineAuditItemRepository
import com.example.merchtools.data.local.repository.OfflineAuditRepository
import com.example.merchtools.data.local.repository.OfflineSkuRepository
import com.example.merchtools.data.local.repository.OfflineStoreRepository
import com.example.merchtools.data.util.ZxingBarcodeGenerator
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.repository.PhotoRepository
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.domain.util.BarcodeGenerator
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

    @Binds
    @Singleton
    abstract fun bindSkuRepository(
        offlineSkuRepository: OfflineSkuRepository
    ): SkuRepository


    /*@Binds
    @Singleton
    abstract fun bindPhotoRepository(
        offlinePhotoRepository: OfflineStoreRepository
    ): PhotoRepository*/

    @Binds
    @Singleton
    abstract fun bindAuditRepository(
        offlineAuditRepository: OfflineAuditRepository
    ): AuditRepository

    @Binds
    @Singleton
    abstract fun bindAuditItemRepository(
        offlineAuditItemRepository: OfflineAuditItemRepository
    ): AuditItemRepository

    @Binds
    @Singleton
    abstract fun bindBarcodeGenerator(
        impl: ZxingBarcodeGenerator
    ): BarcodeGenerator
}