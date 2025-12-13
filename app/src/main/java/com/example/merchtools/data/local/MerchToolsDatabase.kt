package com.example.merchtools.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.merchtools.data.local.dao.AuditDao
import com.example.merchtools.data.local.dao.AuditItemDao
import com.example.merchtools.data.local.dao.PhotoDao
import com.example.merchtools.data.local.dao.SectionDao
import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.data.local.dao.StoreDao
import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.entity.PhotoEntity
import com.example.merchtools.data.local.entity.SectionEntity
import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.data.local.entity.StoreEntity
import com.example.merchtools.data.util.InstantConverter

/**
 * Database class with a singleton Instance object
 */
@Database(
    entities = [
        StoreEntity::class,
        SectionEntity::class,
        SkuEntity::class,
        AuditEntity::class,
        AuditItemEntity::class,
        PhotoEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(InstantConverter::class)
abstract class MerchToolsDatabase : RoomDatabase() {
    abstract val storeDao: StoreDao
    abstract val sectionDao: SectionDao
    abstract val skuDao: SkuDao
    abstract val auditDao: AuditDao
    abstract val auditItemDao: AuditItemDao
    abstract val photoDao: PhotoDao
}