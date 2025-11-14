package com.example.merchtools.data.local

import com.example.merchtools.data.local.dao.StoreDao
import com.example.merchtools.data.local.entity.StoreEntity
import com.example.merchtools.data.local.relations.StoreWithSections
import com.example.merchtools.data.local.repository.StoreRepository
import kotlinx.coroutines.flow.Flow

class OfflineStoreRepository(private val storeDao: StoreDao) : StoreRepository {
    override fun getAllStoresStream(): Flow<List<StoreEntity>> = storeDao.getAllStores()

    override fun getStoreStream(storeId: Long): Flow<StoreEntity?> = storeDao.getStore(storeId)

    override suspend fun insertStore(store: StoreEntity) = storeDao.insert(store)

    override suspend fun deleteStore(store: StoreEntity) = storeDao.delete(store)

    override suspend fun updateStore(store: StoreEntity) = storeDao.update(store)

    override suspend fun getStoreWithSections(storeId: Long) = storeDao.getStoreWithSections(storeId)
}