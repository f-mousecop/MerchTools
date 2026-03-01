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

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.model.Store
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.domain.validation.TextInputFieldValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Recommendation is to switch to Channel for one-time events, such as showing
    // snackbar message from what I've read online
    private val _uiEffect = Channel<StoreCatalogUiEffect>(capacity = Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()


    private val _isAddStoreDialogOpen = MutableStateFlow(false)

    private val _newStoreName = MutableStateFlow("")

    private val _stores = storeRepository.getAllStoresStream()


    val state: StateFlow<StoreState> = combine(
        _isAddStoreDialogOpen,
        _newStoreName,
        _stores,
    ) { isDialogOpen, newStoreName, storesResource ->

        val stores = (storesResource as? Resource.Success)?.data.orEmpty()
        val isLoading = (storesResource as? Resource.Loading)?.isLoading ?: false
        val error = (storesResource as? Resource.Error)?.message

        StoreState(
            stores = stores,
            isAddStoreDialogOpen = isDialogOpen,
            newStoreName = newStoreName,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StoreState(isLoading = true)
    )

    fun onEvent(event: StoreCatalogEvent) {
        when (event) {
            is StoreCatalogEvent.ShowAddStoreDialog -> {
                _isAddStoreDialogOpen.value = true
            }
            is StoreCatalogEvent.HideAddStoreDialog -> {
                _isAddStoreDialogOpen.value = false
                _newStoreName.value = ""
            }
            is StoreCatalogEvent.OnStoreNameChanged -> {
                _newStoreName.value = TextInputFieldValidator.capInputLength(event.name)
            }
            is StoreCatalogEvent.AddNewStore -> {
                addNewStore(_newStoreName.value)
                _isAddStoreDialogOpen.value = false
                _newStoreName.value = ""
            }
            is StoreCatalogEvent.RemoveStore -> {
                removeStore(event.store)
            }
        }
    }

    private fun removeStore(store: Store) {
        viewModelScope.launch {
            try {
                storeRepository.deleteStore(store)
                _uiEffect.send(StoreCatalogUiEffect.ShowMessage("Deleted store: ${store.name}"))
            } catch (e: SQLiteException) {
                _uiEffect.send(StoreCatalogUiEffect.ShowMessage("Error deleting store: ${store.name}: ${e.message}"))
            }
        }
    }

    private fun addNewStore(name: String) {
        // Trim trailing spaces before inserting into the database
        val storeName = TextInputFieldValidator.trimTrailingSpaces(name)

        viewModelScope.launch {
            storeRepository.insertStore(Store(name = storeName))
            _uiEffect.send(StoreCatalogUiEffect.ShowMessage("Store $storeName added successfully"))
        }
    }
}