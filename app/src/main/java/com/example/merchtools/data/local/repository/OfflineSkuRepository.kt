package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.data.mappers.toSku
import com.example.merchtools.data.mappers.toSkuEntity
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.util.Resource
import kotlinx.coroutines.flow.Flow
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

    override fun getSkuStream(skuId: Long): Flow<Resource<Sku?>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                skuDao.getSku(skuId).map { entity ->
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