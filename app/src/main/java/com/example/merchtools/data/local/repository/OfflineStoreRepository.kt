package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.StoreDao
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.data.mappers.toDomain
import com.example.merchtools.data.mappers.toStore
import com.example.merchtools.data.mappers.toStoreEntity
import com.example.merchtools.domain.model.Store
import com.example.merchtools.domain.model.Section
import com.example.merchtools.data.local.relations.StoreWithSections
import com.example.merchtools.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineStoreRepository @Inject constructor(
    val storeDao: StoreDao
) : StoreRepository {
    override fun getAllStoresStream(): Flow<Resource<List<Store>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                // Start listening to the DAOs flow
                storeDao.getAllStores().map { entityList ->
                    // For each list emitted by the DAO, map entities to domain model
                    entityList.map { it.toStore() }
                }.collect { storeList ->
                    // Emit Success with the data for each new list
                    emit(Resource.Success(storeList))
                }
            } catch (e: Exception) {
                // Emit Error if an exception occurs
                e.printStackTrace()
                emit(Resource.Error("An error occurred: ${e.message}"))
            } finally {
                // Emit a final loading(false) state when the flow completes
                // or is canceled
                emit(Resource.Loading(false))
            }
        }
    }

    override fun getStoreStream(storeId: Long): Flow<Resource<Store>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                storeDao.getStore(storeId).map { entity ->
                    // If the entity is not null, map it. Otherwise keep it null
                    entity?.toStore()
                }.collect { store ->
                    emit(Resource.Success(store))
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("An error occurred: ${e.message}"))
            }
            finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun insertStore(store: Store) {
        // We must map the domain model back to an entity before giving it to the
        // DAO
        storeDao.insert(store.toStoreEntity())
    }

    override suspend fun deleteStore(store: Store) {
        storeDao.delete(store.toStoreEntity())
    }

    override suspend fun updateStore(store: Store) {
        storeDao.update(store.toStoreEntity())
    }

    /**
     * We need to grab [Store] and any assigned/recorded
     * [Section] for that store
     * @param storeId The ID of the store we want to get the data from
     * @return A [Store] object with its [Section]s
     * @see StoreDao.getStoreWithSections
     * @see StoreWithSections
     */
    override fun getStoreWithSections(storeId: Long): Resource<Store> {
        return try {
            // Show loading state before operation begins
            // ViewModel handles this

            // Perform the db operation
            val storeWithSections = storeDao.getStoreWithSections(storeId)

            // Map the result to the domain layer and wrap in Resource.Success
            // If storeWithSections is null, toDomain() will also be null
            val domainStore = storeWithSections?.toDomain()
            Resource.Success(domainStore)
        }
        catch (e: Exception) {
            // If an exception occurs, return Resource.Error
            e.printStackTrace()
            Resource.Error("An error occurred: ${e.message}")
        }
    }
}