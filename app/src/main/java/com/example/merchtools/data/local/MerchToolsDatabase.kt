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
    version = 1,
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


    /*abstract fun storeDao(): StoreDao
    abstract fun sectionDao(): SectionDao
    abstract fun skuDao(): SkuDao
    abstract fun auditDao(): AuditDao
    abstract fun auditItemDao(): AuditItemDao
    abstract fun photoDao(): PhotoDao*/

    /*companion object {
        @Volatile
        private var Instance: MerchToolsDatabase? = null

        fun getDatabase(context: Context): MerchToolsDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MerchToolsDatabase::class.java,
                    "merch_tools_database"
                )
                    *//**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     *//*
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }*/
}