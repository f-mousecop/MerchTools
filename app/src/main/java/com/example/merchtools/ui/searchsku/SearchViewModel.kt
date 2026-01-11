/**
 * Copyright (C) 2026 Charles Clark
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.merchtools.ui.searchsku

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.use_case.AddSkuUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val addSkuUseCase: AddSkuUseCase,
    private val skuRepository: SkuRepository,
    private val searchSkuUseCase: SearchSkuUseCase
): ViewModel() {
    var state by mutableStateOf(SearchSkuState())
        private set

    /**
     * TODO: Begin working on using StateFlow for UI state
     */
    /*private val _skuCatalogUiState = MutableStateFlow(SearchSkuState())
    val skuCatalogUiState: StateFlow<SearchSkuState> = _skuCatalogUiState.asStateFlow()*/

    /*private val _uiEvent = MutableSharedFlow<SearchSkuEvent>()
    val uiEvent = _uiEvent.asSharedFlow()*/


    private val _uiEffect = MutableSharedFlow<SearchSkuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var searchJob: Job? = null
    private var skuListJob: Job? = null

    init {
        getAllSkusStream()
    }

    fun onEvent(event: SearchSkuEvent) {
        when (event) {
            is SearchSkuEvent.BarcodeScanned -> {
                viewModelScope.launch {
                    _uiEffect.emit(SearchSkuUiEffect.NavigateToScanBarcode)
                }
            }
            is SearchSkuEvent.AddNewSku -> {
                addNewSku(event.upc)
            }
            is SearchSkuEvent.EditSku -> {
                editSku(event.skuId)
            }
            is SearchSkuEvent.RemoveSku -> {
                removeSku(event.sku)
            }
            is SearchSkuEvent.OnSearchQueryChange -> {
                val query = event.query
                state = state.copy(searchQuery = query)

                if (query.isBlank()) {
                    searchJob?.cancel()
                    Log.d("SearchViewModel", "Search query is blank, cancelling $searchJob")
                    getAllSkusStream()
                } else {
                    skuListJob?.cancel()
                    Log.d("SearchViewModel", "Search query is not blank, cancelling $skuListJob")
                    searchAllSkus(query)
                }
            }
        }
    }

    private fun removeSku(sku: Sku) {
        viewModelScope.launch {
            try {
                skuRepository.delete(sku)
            } catch (e: Exception) {
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage("An unexpected error occurred: ${e.message}"))
                Log.e("SearchViewModel", "Error in removeSku: ${e.message}")
            }
        }
    }

    private fun editSku(skuId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(SearchSkuUiEffect.NavigateToSkuDetails(skuId))
        }
    }

    private fun addNewSku(upc: String) {
        viewModelScope.launch {
            try {
                /**
                 * We check to see if SKU exists in database by fetching by UPC
                 * that was returned from the barcode scan result
                 *
                 * If the UPC is not null, then perform a search query on the SKU catalog
                 */
                val existingSku = skuRepository.getSkuByUpc(upc)
                if (existingSku != null) {
                    state = state.copy(searchQuery = upc)
                    skuListJob?.cancel()
                    searchAllSkus(upc)
                    return@launch
                }

                // Otherwise insert a new SKU into the database via UPC result
                val newSku = addSkuUseCase(upc)
                val newSkuId = newSku.skuId

                // Next navigate to the edit SKU screen
                if (upc.isNotBlank()) {
                    _uiEffect.emit(SearchSkuUiEffect.ShowMessage("SKU not found, adding new UPC $upc to database"))
                    delay(1000L)
                }
                _uiEffect.emit(SearchSkuUiEffect.NavigateToSkuDetails(newSkuId))

            } catch (e: Exception) {
                _uiEffect.emit(
                    SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error")
                )
            }
        }
    }

    private fun searchAllSkus(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            Log.d("SearchViewModel", "Searching for '$query', $searchJob started")
            searchSkuUseCase
                .catalog(query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { skus ->
                                state = state.copy(
                                    skus = skus,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(error = result.message)
                            _uiEffect.emit(
                                SearchSkuUiEffect.ShowMessage(result.message.toString())
                            )
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }

    }

    private fun getAllSkusStream() {
        skuListJob?.cancel()
        skuListJob = viewModelScope.launch {
            Log.d("SearchViewModel", "Getting all SKUs, $skuListJob started")
            skuRepository
                .getAllSkusStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { skus ->
                                state = state.copy(
                                    skus = skus,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }

        }
    }
}