package com.example.merchtools.ui.searchsku

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.data.local.mock.MockSkus
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.use_case.AddSkuUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import com.example.merchtools.core.Resource
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
    private val savedStateHandle: SavedStateHandle,
    private val skuRepository: SkuRepository,
    private val searchSkuUseCase: SearchSkuUseCase
): ViewModel() {
    var state by mutableStateOf(SearchSkuState())
        private set

    private val _uiEffect = MutableSharedFlow<SearchSkuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var searchJob: Job? = null
    private var skuListJob: Job? = null

    init {
        getAllSkusStream()
    }

    fun onEvent(event: SearchSkuEvent) {
        when (event) {
            is SearchSkuEvent.Refresh -> {
                getAllSkusStream()
                viewModelScope.launch {
                    seedMockSkus()
                }
            }
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
                searchAllSkus(event.query)
            }
        }
    }

    private fun removeSku(sku: Sku) {
        val currentSkus = state.skus.toMutableList()
        viewModelScope.launch {
            try {
                state = state.copy(
                    skus = currentSkus
                )

                skuRepository.delete(sku)
            } catch (e: Exception) {
                _uiEffect.emit(SearchSkuUiEffect.ShowMessage(e.message ?: "Unknown error"))
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
        state = state.copy(searchQuery = query)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchSkuUseCase
                .catalog(query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { skus ->
                                state = state.copy(
                                    skus = skus
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(error = result.message)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }

    }

    private suspend fun seedMockSkus() {
        MockSkus.skus.forEach { sku ->
            skuRepository.insert(sku)
        }
    }

    private fun getAllSkusStream() {
        skuListJob?.cancel()
        skuListJob = viewModelScope.launch {
            skuRepository
                .getAllSkusStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { skus ->
                                state = state.copy(
                                    skus = skus
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(error = result.message)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }

        }
    }
}