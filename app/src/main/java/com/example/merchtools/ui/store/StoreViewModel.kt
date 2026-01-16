package com.example.merchtools.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.model.Store
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.domain.validation.TextInputFieldValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<StoreCatalogUiEffect>()
    val uiEffect: SharedFlow<StoreCatalogUiEffect> = _uiEffect.asSharedFlow()

    private val _isAddStoreDialogOpen = MutableStateFlow(false)
    private val _newStoreName = MutableStateFlow("")

    private val _stores = storeRepository
        .getAllStoresStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading(true)
        )

    val state: StateFlow<StoreState> = combine(
        _isAddStoreDialogOpen,
        _newStoreName,
        _stores,
    ) { isDialogOpen, newStoreName, storesResource ->
        when (storesResource) {
            is Resource.Loading -> {
                StoreState(isLoading = storesResource.isLoading)
            }
            is Resource.Error -> {
                StoreState(error = storesResource.message)
            }
            is Resource.Success -> {
                StoreState(
                    stores = storesResource.data ?: emptyList(),
                    isAddStoreDialogOpen = isDialogOpen,
                    newStoreName = newStoreName
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StoreState(isLoading = true)
    )

    fun onEvent(event: StoreCatalogEvent) {
        when (event) {
            is StoreCatalogEvent.ShowAddStoreDialog -> {
                _isAddStoreDialogOpen.value = true
            }
            is StoreCatalogEvent.HideAddStoreDialog -> {
                _isAddStoreDialogOpen.value = false
            }
            is StoreCatalogEvent.OnStoreNameChanged -> {
                _newStoreName.value = TextInputFieldValidator.capInputLength(event.name)
            }
            is StoreCatalogEvent.AddNewStore -> {
                addNewStore(state.value.newStoreName)
                _isAddStoreDialogOpen.value = false
                _newStoreName.value = ""
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
        viewModelScope.launch {
            try {
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

    /**
     * Probably will delete this as it is not used or needed
     */
    private fun editStore(storeId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(StoreCatalogUiEffect.NavigateToEditStore)
        }
    }

    private fun addNewStore(name: String) {
        // Trim trailing spaces before inserting into the database
        val storeName = TextInputFieldValidator.trimTrailingSpaces(name)

        viewModelScope.launch {
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
}
