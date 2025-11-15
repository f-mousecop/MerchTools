package com.example.merchtools.data.local

import com.example.merchtools.data.local.dao.StoreDao
import com.example.merchtools.data.local.repository.StoreRepository
import com.example.merchtools.data.mappers.toDomain
import com.example.merchtools.data.mappers.toStore
import com.example.merchtools.data.mappers.toStoreEntity
import com.example.merchtools.domain.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineStoreRepository(val storeDao: StoreDao) : StoreRepository {
    override fun getAllStoresStream(): Flow<List<Store>> {
        return storeDao.getAllStores().map { entityList ->
            // For each list emitted by the flow, map every entity in that list to a domain model.
            entityList.map { it.toStore() }
        }
    }

    override fun getStoreStream(storeId: Long): Flow<Store?> {
        return storeDao.getStore(storeId).map { entity ->
            // If the entity is not null, map it. Otherwise keep it null
            entity?.toStore()
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

    override fun getStoreWithSections(storeId: Long): Store? {
        // Call the DAO to get the data-layer object
        val storeWithSections = storeDao.getStoreWithSections(storeId)

        // Convert the data-layer object to the domain object
        // ?.let is a safe call that only executes the block if the result is not null
        return storeWithSections?.toDomain()
    }
}