package com.example.merchtools

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSkuRepository : SkuRepository {

    private val skus = mutableListOf<Sku>()
    private var autoIncrementId = 1L

    override fun getAllSkusStream(): Flow<Resource<List<Sku>>> = flow {
        emit(Resource.Success(skus.toList()))
    }

    override fun getSkyByIdStream(skuId: Long): Flow<Sku?> = flow {
        emit(skus.firstOrNull { it.skuId == skuId })
    }

    override fun getSkuStream(upc: String): Flow<Resource<Sku?>> = flow {
        val sku = skus.firstOrNull { it.upc == upc }
        emit(Resource.Success(sku))
    }

    override suspend fun getSkuByUpc(upc: String): Sku? {
        return skus.firstOrNull { it.upc == upc }
    }

    override suspend fun insert(sku: Sku): Long {
        val newId = autoIncrementId++
        val newSku = sku.copy(skuId = newId)
        skus.add(newSku)
        return newId
    }

    override suspend fun upsertAndReturnId(sku: Sku): Long {
        val index = skus.indexOfFirst { it.skuId == sku.skuId }
        return if (index >= 0) {
            skus[index] = sku
            sku.skuId
        } else {
            insert(sku)
        }
    }

    override suspend fun delete(sku: Sku) {
        skus.removeIf { it.skuId == sku.skuId }
    }

    override suspend fun update(sku: Sku) {
        val index = skus.indexOfFirst { it.skuId == sku.skuId }
        if (index >= 0) {
            skus[index] = sku
        }
    }

    override fun searchSkus(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<Sku>>> = flow {
        val filtered = skus.filter { sku ->
            sku.name.contains(query, ignoreCase = true) ||
                    sku.brand.contains(query, ignoreCase = true) ||
                    sku.upc.contains(query)
        }
        emit(Resource.Success(filtered))
    }

    // Helper for test
    suspend fun addSkuForTest(sku: Sku): Long = insert(sku)

    fun clear() {
        skus.clear()
        autoIncrementId = 1L
    }
}