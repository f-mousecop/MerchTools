package com.example.merchtools.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.merchtools.data.local.entity.SkuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sku: SkuEntity): Long

    @Update
    suspend fun update(sku: SkuEntity)

    @Delete
    suspend fun delete(sku: SkuEntity)

    @Query("SELECT * FROM skus WHERE skuId = :id")
    fun getSku(id: Long): Flow<SkuEntity?>

    @Query("SELECT * FROM skus ORDER BY brand, name")
    fun getAllSkus(): Flow<List<SkuEntity>>

    @Query("""
        SELECT * FROM skus
        WHERE name LIKE '%' || :query || '%'
        ORDER BY brand, name
    """)
    fun searchSkus(query: String): Flow<List<SkuEntity>>
}