package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.data.mappers.toSku
import com.example.merchtools.data.mappers.toSkuEntity
import com.example.merchtools.domain.model.Sku
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineSkuRepository @Inject constructor(val skuDao: SkuDao) : SkuRepository {
    override fun getAllSkusStream(): Flow<List<Sku>> {
        return skuDao.getAllSkus().map { entityList ->
            // For each list emitted by the flow, map every entity in that list
            entityList.map { it.toSku() }
        }
    }

    override fun getSkuStream(skuId: Long): Flow<Sku?> {
        return skuDao.getSku(skuId).map { entity ->
            // If the entity is not null, map it. Otherwise keep it null
            entity?.toSku()
        }
    }

    override suspend fun insert(sku: Sku) {
        // We must map the domain model back to an entity before giving it back
        // to the DAO
        skuDao.insert(sku.toSkuEntity())
    }

    override suspend fun delete(sku: Sku) {
        skuDao.delete(sku.toSkuEntity())
    }

    override suspend fun update(sku: Sku) {
        skuDao.update(sku.toSkuEntity())
    }

    override fun searchSkus(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<List<Sku>> {
        return skuDao.searchSkus(query).map { entityList ->
            entityList.map { it.toSku() }
        }
    }
}