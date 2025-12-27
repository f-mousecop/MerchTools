package com.example.merchtools.data.local.repository

import android.util.Log
import androidx.sqlite.SQLiteException
import com.example.merchtools.data.local.dao.SkuDao
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.data.mappers.toSku
import com.example.merchtools.data.mappers.toSkuEntity
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.core.Resource
import com.example.merchtools.data.local.entity.SkuEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineSkuRepository @Inject constructor(val skuDao: SkuDao) : SkuRepository {
    override fun getAllSkusStream(): Flow<Resource<List<Sku>>> {
        return skuDao.getAllSkus()
            .map { entityList ->
                Resource.Success(entityList.map { it.toSku() }) as Resource<List<Sku>>
            }
            .onStart { emit(Resource.Loading(true)) }
            .catch { e ->
                Log.e("SkuRepository", "Error in searchSkus: ${e.message}")

                val errorMessage = when (e) {
                    is IOException -> "Network error, please check your connection: ${e.localizedMessage}"
                    is SQLiteException -> "A local database error occurred: ${e.localizedMessage}"
                    else -> "An unexpected error occurred: ${e.localizedMessage}"
                }
                emit(Resource.Error(errorMessage))
            }
            .onCompletion {
                emit(Resource.Loading(false))
            }
    }

    override fun getSkyByIdStream(skuId: Long): Flow<Sku?> {
        return skuDao.getSkuById(skuId).map { entity ->
            entity?.toSku()
        }
    }

    override fun getSkuStream(upc: String): Flow<Resource<Sku?>> {
        return skuDao.getSkuByUpc(upc)
            .map<SkuEntity?, Resource<Sku?>> { entity ->
                Resource.Success(entity?.toSku())
            }
            .onStart { emit(Resource.Loading(true)) }
            .catch { e ->
                Log.e("SkuRepository", "Error in searchSkus: ${e.message}")

                val errorMessage = when (e) {
                    is IOException -> "Network error, please check your connection: ${e.localizedMessage}"
                    is SQLiteException -> "A local database error occurred: ${e.localizedMessage}"
                    else -> "An unexpected error occurred: ${e.localizedMessage}"
                }
                emit(Resource.Error(errorMessage))
            }
            .onCompletion {
                emit(Resource.Loading(false))
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
        return skuDao.searchSkus(query)
            .map { entityList ->
                Resource.Success(entityList.map { it.toSku() }) as Resource<List<Sku>>
            }
            .onStart { emit(Resource.Loading(true)) }
            .catch { e ->
                Log.e("SkuRepository", "Error in searchSkus: ${e.message}")

                val errorMessage = when (e) {
                    is IOException -> "Network error, please check your connection: ${e.localizedMessage}"
                    is SQLiteException -> "A local database error occurred: ${e.localizedMessage}"
                    else -> "An unexpected error occurred: ${e.localizedMessage}"
                }
                emit(Resource.Error(errorMessage))
            }
            .onCompletion {
                emit(Resource.Loading(false))
            }
    }
}