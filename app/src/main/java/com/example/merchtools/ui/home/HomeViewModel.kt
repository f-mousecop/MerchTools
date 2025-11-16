package com.example.merchtools.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.data.local.mock.MockStores
import com.example.merchtools.domain.model.Store
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    var state by mutableStateOf(HomeState())

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
//            seedMockStores()
            getAllStoresStream()
        }
    }

    /**
     * MOCK store list to display on home screen
     */
    /*private suspend fun seedMockStores() {
        MockStores.stores.forEach { store ->
            storeRepository.insertStore(store)
        }
    }*/

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> {
                getAllStoresStream(fetchFromRemote = false)
            }
            is HomeEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getAllStoresStream()
                }
            }
        }
    }

    private fun getAllStoresStream(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            storeRepository
                .getAllStoresStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { stores ->
                                state = state.copy(
                                    stores = stores
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    fun onButtonClicked() {
        viewModelScope.launch {
            storeRepository.deleteStore(MockStores.stores[2])
        }
    }
}