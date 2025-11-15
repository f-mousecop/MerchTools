package com.example.merchtools.domain.repository
import com.example.merchtools.data.local.repository.OfflineStoreRepository
import com.example.merchtools.domain.model.Store
import com.example.merchtools.util.Resource
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
    fun getAllStoresStream(): Flow<List<Store>>

    /**
     * Retrieve a store from the data source with the storeId
     * @see storeId
     * */
    fun getStoreStream(storeId: Long): Flow<Store?>

    /**
     * Insert a store into the data source
     * @see store
     * */
    suspend fun insertStore(store: Store)

    /**
     * Delete store from the data source
     * @see store
     */
    suspend fun deleteStore(store: Store)

    /**
     * Update store in the data source
     * @see store
     */
    suspend fun updateStore(store: Store)

    /**
     * Retrieves the store with all associated sections, if applicable
     * @see StoreWithSections
     * @see OfflineStoreRepository.getStoreWithSections
     */
    fun getStoreWithSections(storeId: Long): Store?
}
