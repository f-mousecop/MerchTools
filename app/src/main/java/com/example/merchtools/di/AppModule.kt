package com.example.merchtools.di

import android.app.Application
import androidx.room.Room
import com.example.merchtools.data.local.MerchToolsDatabase
import com.example.merchtools.data.local.dao.StoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        ).build()
    }

    @Provides
    @Singleton
    fun providesStoreDao(db: MerchToolsDatabase): StoreDao {
        return db.storeDao
    }
}