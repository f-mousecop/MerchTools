/**
 * Copyright (C) 2026 Charles Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.merchtools.di

import android.app.Application
import androidx.room.Room
import com.example.merchtools.data.local.MerchToolsDatabase
import com.example.merchtools.data.local.dao.AuditDao
import com.example.merchtools.data.local.dao.AuditItemDao
import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.data.local.dao.StoreDao
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMerchToolsDatabase(app: Application): MerchToolsDatabase {
        return Room.databaseBuilder(
            app,
            MerchToolsDatabase::class.java,
            "merch_tools_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun providesStoreDao(db: MerchToolsDatabase): StoreDao {
        return db.storeDao
    }

    @Provides
    @Singleton
    fun providesSkuDao(db: MerchToolsDatabase): SkuDao {
        return db.skuDao
    }

    @Provides
    @Singleton
    fun providesAuditDao(db: MerchToolsDatabase) : AuditDao {
        return db.auditDao
    }

    @Provides
    @Singleton
    fun providesAuditItemDao(db: MerchToolsDatabase) : AuditItemDao {
        return db.auditItemDao
    }

    @Provides
    @Singleton
    fun provideClock(): Clock {
        return Clock.systemUTC()
    }

    @Provides
    @Singleton
    fun providesBarcodeScanner(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128
            ).build()
        return BarcodeScanning.getClient(options)
    }
}