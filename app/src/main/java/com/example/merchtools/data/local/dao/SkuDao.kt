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

    @Query("SELECT * FROM skus WHERE skuId = :skuId LIMIT 1")
    fun getSkuById(skuId: Long): Flow<SkuEntity?>

    @Query("SELECT * FROM skus WHERE upc = :upc LIMIT 1")
    fun getSkuByUpc(upc: String): Flow<SkuEntity?>

    @Query("SELECT * FROM skus ORDER BY brand")
    fun getAllSkus(): Flow<List<SkuEntity>>

    @Query("""
        SELECT * FROM skus
        WHERE lower(name) LIKE '%' || LOWER(:query) || '%'
        OR lower(brand) LIKE '%' || LOWER(:query) || '%'
        OR lower(upc) LIKE '%' || LOWER(:query) || '%'
        ORDER BY brand
    """)
    fun searchSkus(query: String): Flow<List<SkuEntity>>
}