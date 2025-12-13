package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.data.mappers.toSku
import com.example.merchtools.data.mappers.toSkuEntity
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineSkuRepository @Inject constructor(val skuDao: SkuDao) : SkuRepository {
    override fun getAllSkusStream(): Flow<Resource<List<Sku>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                skuDao.getAllSkus().map { entityList ->
                    // For each list emitted by the flow, map every entity in that list
                    entityList.map { it.toSku() }
                }.collect { skuList ->
                    emit(Resource.Success(skuList))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override fun getSkyByIdStream(skuId: Long): Flow<Sku?> {
        return skuDao.getSkuById(skuId).map { entity ->
            entity?.toSku()
        }
    }

    override fun getSkuStream(upc: String): Flow<Resource<Sku?>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                skuDao.getSkuByUpc(upc).map { entity ->
                    // If the entity is not null, map it. Otherwise keep it null
                    entity?.toSku()
                }.collect { sku ->
                    emit(Resource.Success(sku))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getSkuByUpc(upc: String): Sku? {
        return skuDao.getSkuByUpc(upc).firstOrNull()?.toSku()
    }

    override suspend fun insert(sku: Sku): Long {
        // We must map the domain model back to an entity before giving it back
        // to the DAO
        return skuDao.insert(sku.toSkuEntity())
    }

    override suspend fun upsertAndReturnId(sku: Sku): Long {
        // Try to insert with IGNORE, returns -1 if UPC already exists
        val insertedId = skuDao.insert(sku.toSkuEntity())
        if (insertedId != -1L) {
            return insertedId
        }

        // If we got -1L, UPC already exists and fetch that row and return its id
        val existing = skuDao.getSkuByUpc(sku.upc).firstOrNull()
            ?: error("SKU with UPC ${sku.upc} should exist but was not found")
        return existing.skuId
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
    ): Flow<Resource<List<Sku>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                skuDao.searchSkus(query).map { entityList ->
                    entityList.map { it.toSku() }
                }.collect { skuList ->
                    emit(Resource.Success(skuList))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }
}