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

    @Query("SELECT * from stores LIMIT 1")
    suspend fun getFirstStore(): StoreEntity?

    @Update
    suspend fun update(store: StoreEntity)

    @Delete
    suspend fun delete(store: StoreEntity)

    @Query("SELECT * from stores WHERE storeId = :id")
    fun getStore(id: Long): Flow<StoreEntity?>

    @Query("SELECT * from stores ORDER BY name ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Query(
        """
            SELECT *
            FROM stores
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'
        """
    )
    fun searchStores(query: String): Flow<List<StoreEntity>>

    /** We want to load a given [StoreEntity] with its [SectionEntity]'s
     * returning a list of Sections that belong to the Store. */
    @Transaction
    @Query("SELECT * from stores WHERE storeId = :id")
    fun getStoreWithSections(id: Long): StoreWithSections?
}