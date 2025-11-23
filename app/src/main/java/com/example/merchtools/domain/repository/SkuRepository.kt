package com.example.merchtools.domain.repository

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.util.Resource
import kotlinx.coroutines.flow.Flow

interface SkuRepository {
    /**
     * Retrieve all SKUs from the data source
     * @return List of SKUs
     */
    fun getAllSkusStream(): Flow<Resource<List<Sku>>>

    /**
     * Retrieve a SKU from the data source with the skuId
     * @see [SkuEntity]
     * @return [Sku]
     */
    fun getSkuStream(upc: String): Flow<Resource<Sku?>>

    /**
     * Insert a SKU into the data source
     * @see [SkuEntity]
     */
    suspend fun insert(sku: Sku)

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