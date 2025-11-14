package com.example.merchtools.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.merchtools.data.local.entity.StoreEntity
import com.example.merchtools.data.local.relations.StoreWithSections
import com.example.merchtools.data.local.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(store: StoreEntity): Long

    @Update
    suspend fun update(store: StoreEntity)

    @Delete
    suspend fun delete(store: StoreEntity)

    @Query("SELECT * from stores WHERE storeId = :id")
    fun getStore(id: Long): Flow<StoreEntity?>

    @Query("SELECT * from stores ORDER BY name ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    /** We want to load a given [StoreEntity] with its [SectionEntity]'s
     * returning a list of Sections that belong to the Store. */
    @Transaction
    @Query("SELECT * from stores WHERE storeId = :id")
    fun getStoreWithSections(id: Long): StoreWithSections?
}