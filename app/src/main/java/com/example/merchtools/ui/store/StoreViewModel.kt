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

package com.example.merchtools.ui.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.model.Store
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.domain.validation.TextInputFieldValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel(){
    var state by mutableStateOf(StoreState())
        private set

    private val _uiEffect = MutableSharedFlow<StoreCatalogUiEffect>()
    val uiEffect = _uiEffect

    private var storeListJob: Job? = null
    private var storeAddJob: Job? = null

    init {
        getAllStoresStream()
    }

    fun onEvent(event: StoreCatalogEvent) {
        when (event) {
            is StoreCatalogEvent.ShowAddStoreDialog -> {
                state = state.copy(isAddStoreDialogOpen = true)
            }
            is StoreCatalogEvent.HideAddStoreDialog -> {
                state = state.copy(isAddStoreDialogOpen = false)
            }
            is StoreCatalogEvent.OnStoreNameChanged -> {
                state = state.copy(
                    newStoreName = TextInputFieldValidator.capInputLength(event.name)
                )
            }
            is StoreCatalogEvent.AddNewStore -> {
                addNewStore(state.newStoreName)
                state = state.copy(
                    isAddStoreDialogOpen = false,
                    newStoreName = ""
                )
            }
            is StoreCatalogEvent.RemoveStore -> {
                removeStore(event.store)
            }
            is StoreCatalogEvent.EditStore -> {
                editStore(event.storeId)
            }
        }
    }

    private fun removeStore(store: Store) {
        val currentStores = state.stores.toMutableList()
        viewModelScope.launch {
            try {
                state = state.copy(
                    stores = currentStores
                )

                storeRepository.deleteStore(store)
                _uiEffect.emit(
                    StoreCatalogUiEffect.ShowMessage(
                        "Deleted store: ${store.name}"
                    )
                )
            } catch (e: Exception) {
                _uiEffect.emit(StoreCatalogUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun editStore(storeId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(StoreCatalogUiEffect.NavigateToEditStore)
        }
    }

    private fun addNewStore(name: String) {
        // Trim trailing spaces before inserting into the database
        val storeName = TextInputFieldValidator.trimTrailingSpaces(name)

        storeAddJob?.cancel()
        storeAddJob = viewModelScope.launch {
            try {
                storeRepository.insertStore(
                    Store(
                        name = storeName
                    )
                )
                _uiEffect.emit(
                    StoreCatalogUiEffect.ShowMessage(
                        "Store $storeName added successfully"
                    )
                )
            } catch (e: Exception) {
                _uiEffect.emit(
                    StoreCatalogUiEffect.ShowMessage(
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    private fun getAllStoresStream() {
        storeListJob?.cancel()
        storeListJob = viewModelScope.launch {
            storeRepository
                .getAllStoresStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                state = state.copy(
                                    stores = it,
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