package com.example.merchtools.data.local.repository

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.data.local.entity.SkuEntity
import kotlinx.coroutines.flow.Flow

interface SkuRepository {
    /**
     * Retrieve all SKUs from the data source
     * @return List of SKUs
     */
    fun getAllSkusStream(): Flow<List<Sku>>

    /**
     * Retrieve a SKU from the data source with the skuId
     * @see [SkuEntity]
     * @return [Sku]
     */
    fun getSkuStream(skuId: Long): Flow<Sku?>

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

    fun searchSkus(query: String): Flow<List<Sku>>
}