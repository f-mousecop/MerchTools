package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.entity.StoreEntity
import com.example.merchtools.data.local.relations.StoreWithSections
import kotlinx.coroutines.flow.Flow

/**
 * Repo that provides insert, update, delete, and retrieve of
 * [com.example.merchtools.data.local.entity.StoreEntity] from a given data source
 */
interface StoreRepository {
    /**
     * Retrieve all Stores from the data source
     * @return List of Stores
     */
    fun getAllStoresStream(): Flow<List<StoreEntity>>

    /**
     * Retrieve a store from the data source with the storeId
     * @see storeId
     * */
    fun getStoreStream(storeId: Long): Flow<StoreEntity?>

    /**
     * Insert a store into the data source
     * @see store
     * */
    suspend fun insertStore(store: StoreEntity): Long

    /**
     * Delete store from the data source
     * @see store
     */
    suspend fun deleteStore(store: StoreEntity)

    /**
     * Update store in the data source
     * @see store
     */
    suspend fun updateStore(store: StoreEntity)

    /**
     * Retrieves the store with all associated sections, if applicable
     */
    suspend fun getStoreWithSections(storeId: Long): StoreWithSections?
}
