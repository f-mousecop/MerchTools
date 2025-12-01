package com.example.merchtools.ui.searchsku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.data.local.mock.MockSkus
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.use_case.AddSkuUseCase
import com.example.merchtools.util.Resource
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
    private val skuRepository: SkuRepository
): ViewModel() {
    var state by mutableStateOf(SearchSkuState())
        private set

    private val _uiEffect = MutableSharedFlow<SearchSkuUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var searchJob: Job? = null


    init {
        viewModelScope.launch {
            getAllSkusStream()
        }
    }

    fun onEvent(event: SearchSkuEvent) {
        when (event) {
            is SearchSkuEvent.Refresh -> {
                getAllSkusStream()
                viewModelScope.launch {
                    seedMockSkus()
                }
            }
            is SearchSkuEvent.AddNewSku -> {
                addNewSku()
            }
            is SearchSkuEvent.EditSku -> {
                editSku(event.skuId)
            }
            is SearchSkuEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    searchAllSkus()
                }
            }
        }
    }

    private fun editSku(skuId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(SearchSkuUiEffect.NavigateToSkuDetails(skuId))
        }
    }

    private fun addNewSku() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val newSku = addSkuUseCase()
                val newSkuId = newSku.skuId

                val currentItems = state.skus
                state = state.copy(
                    skus = currentItems + newSku
                )
                _uiEffect.emit(
                    SearchSkuUiEffect.NavigateToSkuDetails(newSkuId)
                )

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
        viewModelScope.launch {
            skuRepository
                .searchSkus(false, query)
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
        viewModelScope.launch {
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