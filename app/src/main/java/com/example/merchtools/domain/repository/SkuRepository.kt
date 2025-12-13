package com.example.merchtools.domain.repository

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.core.Resource
import kotlinx.coroutines.flow.Flow

interface SkuRepository {
    /**
     * Retrieve all SKUs from the data source
     * @return List of SKUs
     */
    fun getAllSkusStream(): Flow<Resource<List<Sku>>>

    fun getSkyByIdStream(skuId: Long): Flow<Sku?>

    /**
     * Retrieve a SKU from the data source with the skuId
     * @see [SkuEntity]
     * @return [Sku]
     */
    fun getSkuStream(upc: String): Flow<Resource<Sku?>>

    suspend fun getSkuByUpc(upc: String): Sku?

    /**
     * Insert a SKU into the data source
     * @see [SkuEntity]
     */
    suspend fun insert(sku: Sku): Long

    /**
     * Insert a SKU into the data source and return the new rowId
     * @see [SkuEntity]
     */
    suspend fun upsertAndReturnId(sku: Sku): Long

    /**
     * Delete SKU from the data source
     * @see [SkuEntity]
     */
    suspend fun delete(sku: Sku)

     /**
     * Update SKU in the data source
     * @see [SkuEntity]
     */
    suspend fun update(sku: Sku)

    fun searchSkus(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<Sku>>>
}