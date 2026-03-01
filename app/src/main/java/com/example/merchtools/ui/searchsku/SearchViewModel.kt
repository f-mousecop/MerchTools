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
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.use_case.AddSkuUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val addSkuUseCase: AddSkuUseCase,
    private val skuRepository: SkuRepository,
    private val searchSkuUseCase: SearchSkuUseCase
): ViewModel() {
    /*private val _uiEffect = MutableSharedFlow<SearchSkuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()*/

    private val _uiEffect = Channel<SearchSkuUiEffect>(capacity = Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _searchQuery = MutableStateFlow(SearchSkuState().searchQuery)
    val searchQuery = _searchQuery.asStateFlow()
    val uiState: StateFlow<SearchSkuState> = _searchQuery
        // Debounce the query to avoid necessary load on the database
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            // We need to switch to the correct stream based on the query
            val flow = if (query.isBlank()) {
                skuRepository.getAllSkusStream()
            } else {
                searchSkuUseCase.catalog(query)
            }
            flow.map { result -> result to query }
        }
        .runningFold(SearchSkuState()) { prev, (result, query) ->
            when (result) {
                is Resource.Loading -> {
                    prev.copy(
                        isLoading = result.isLoading,
                        error = null,
                        searchQuery = query
                    )
                }
                is Resource.Success -> {
                    prev.copy(
                        skus = result.data.orEmpty(),
                        isLoading = false,
                        error = null,
                        searchQuery = query
                    )
                }
                is Resource.Error -> {
                    prev.copy(
                        isLoading = false,
                        error = result.message,
                        searchQuery = query
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchSkuState(isLoading = true)
        )

    fun onEvent(event: SearchSkuEvent) {
        when (event) {
            is SearchSkuEvent.OnSearchQueryChange -> _searchQuery.value = event.query

            is SearchSkuEvent.AddNewSku -> addNewSku(event.upc)

            is SearchSkuEvent.BarcodeScanned -> viewModelScope.launch {
                _uiEffect.send(SearchSkuUiEffect.NavigateToScanBarcode)
            }
            is SearchSkuEvent.EditSku -> viewModelScope.launch {
                _uiEffect.send(SearchSkuUiEffect.NavigateToSkuDetails(event.skuId))
            }
            is SearchSkuEvent.RemoveSku -> viewModelScope.launch {
                runCatching { skuRepository.delete(event.sku) }
                    .onSuccess {
                        _uiEffect.send(SearchSkuUiEffect.ShowMessage("SKU deleted"))
                    }
                    .onFailure { exception ->
                        uiState.value.copy(error = exception.message)

                        Log.e("SearchViewModel", "SKU deletion failed", exception)

                        _uiEffect.send(SearchSkuUiEffect.ShowMessage(
                            exception.message ?: "An unexpected error occurred")
                        )
                    }
            }
        }
    }

    private fun addNewSku(upc: String) {
        viewModelScope.launch {
            /**
             * We need to prevent SKU with invalid UPCs from being added to the database
             */
            if (!upc.isDigitsOnly()) {
                println("DEBUG: Invalid UPC: $upc")
                _uiEffect.send(SearchSkuUiEffect.ShowMessage("Invalid UPC: $upc"))
                return@launch
            }
            try {
                /**
                 * We check to see if SKU exists in database by fetching by UPC
                 * that was returned from the barcode scan result
                 *
                 * If the UPC is not null, then perform a search query on the SKU catalog
                 */
                val existingSku = searchSkuUseCase.byUpc(upc = upc)
                if (existingSku != null) {
                    _searchQuery.value = upc
                    return@launch
                }

                _uiEffect.send(SearchSkuUiEffect.ShowMessage("SKU not found, adding new UPC $upc to database"))

                // Otherwise insert a new SKU into the database via UPC result
                val newSku = addSkuUseCase(upc)

                // Next navigate to the edit SKU screen
                _uiEffect.send(SearchSkuUiEffect.NavigateToSkuDetails(newSku.skuId))

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiEffect.send(
                    SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error")
                )
            }
        }
    }
}