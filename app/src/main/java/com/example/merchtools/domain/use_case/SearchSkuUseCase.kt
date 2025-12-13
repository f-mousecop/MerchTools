package com.example.merchtools.domain.use_case

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.core.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case responsible for searching for a SKU (Stock Keeping Unit) by UPC or
 * performing a catalog search with a given query.
 *
 * This class acts as an intermediary between the ViewModel/UI layer and the data layer (repository),
 * encapsulating the business logic for SKU searches.
 *
 * @property skuRepository The repository that provides access to SKU data.
 */
class SearchSkuUseCase @Inject constructor(
    private val skuRepository: SkuRepository
) {
    /**
     * Retrieves a single SKU by its Universal Product Code (UPC).
     *
     * This function delegates the call to the repository to find a SKU that matches the provided UPC.
     * It's a direct lookup and is expected to return either one SKU or none.
     *
     * @param upc The 12 or 13-digit Universal Product Code to search for.
     * @return The matching [Sku] object, or `null` if no SKU is found with the given UPC.
     */
    suspend fun byUpc(upc: String): Sku? {
        return skuRepository.getSkuByUpc(upc)
    }


    /**
     * Searches the product catalog for SKUs matching a given query.
     *
     * This function delegates the search operation to the repository, which performs a search
     * based on the provided query string. The results are returned as a Flow of a Resource,
     * allowing the UI to observe loading, success, and error states.
     *
     * @param query The search term to use for finding matching SKUs in the catalog.
     * @return A [Flow] emitting a [Resource] that wraps the list of found [Sku] objects.
     */
    fun catalog(query: String): Flow<Resource<List<Sku>>> {
        return skuRepository.searchSkus(false, query)
    }
}