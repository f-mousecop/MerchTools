package com.example.merchtools.ui.audit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuditViewModel @Inject constructor(
    private val auditRepository: AuditRepository,
    private val skuRepository: SkuRepository,
    private val auditItemRepository: AuditItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auditId: Long = checkNotNull(savedStateHandle["auditId"])
    var state by mutableStateOf(AuditState())
        private set

    private val _uiEffect = MutableSharedFlow<AuditUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var auditJob: Job? = null

    init {
        viewModelScope.launch {
            getAuditStream(auditId)
        }
    }

    fun onEvent(event: AuditEvent) {
        when (event) {
            is AuditEvent.AddNewItem -> {
                addNewItem()
            }
            is AuditEvent.EditAuditItem -> {
                editAuditItem()
            }
            // Do I need to copy the upc from the UI state?
            is AuditEvent.BarcodeScanned -> {
                findSkuByUpc(event.upc)
            }
            is AuditEvent.OnItemChanged -> {
                updateItem(event.itemIndex, event.item)
            }
            is AuditEvent.RemoveItem -> {
                removeItem(event.itemIndex)
            }
            is AuditEvent.AddPhotoToItem -> {
                addPhotoToItem(event.itemIndex)
            }
            is AuditEvent.RemovePhotoFromItem -> {
                removePhotoFromItem(event.itemIndex, event.photoUri)
            }
            is AuditEvent.SaveAudit -> {
                saveAudit()
            }
            is AuditEvent.DiscardChanges -> {
                discardChanges()
            }
        }
    }



    private fun addPhotoToItem(itemIndex: Int) {
        TODO("Not yet implemented")
    }

    private fun removePhotoFromItem(itemIndex: Int, photoUri: String) {
        TODO("Not yet implemented")
    }

    private fun saveAudit() {
        TODO("Not yet implemented")
    }

    private fun discardChanges() {
        TODO("Not yet implemented")
    }

    private fun editAuditItem() {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            try {
                val currentItem = state.audit.items[1]
                _uiEffect.emit(AuditUiEffect.NavigateToEditAuditItem(currentItem.auditItemId))
            } catch (e: Exception) {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun removeItem(itemIndex: Int) {
        val currentItems = state.audit.items.toMutableList()
        try {
            if (itemIndex in currentItems.indices) {
                currentItems.removeAt(itemIndex)
                state = state.copy(
                    audit = state.audit.copy(
                        items = currentItems
                    )
                )
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }

    }

    private fun updateItem(itemIndex: Int, item: AuditItem) {
        val currentItems = state.audit.items.toMutableList()
        try {
            if (itemIndex in currentItems.indices) {
                currentItems[itemIndex] = item
                state = state.copy(
                    audit = state.audit.copy(
                        items = currentItems
                    )
                )
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }

    }

    private fun findSkuByUpc(upc: String) {
        viewModelScope.launch {

            val existingSku = skuRepository.getSkuByUpc(upc)
            val sku = if (existingSku != null) {
                existingSku
            } else {
                _uiEffect.emit(AuditUiEffect.ShowMessage("SKU not found"))
                val placeHolder = if (upc == "123") {
                    Sku(
                        upc = "123",
                        name = "Product 1",
                        casePack = "Case Pack",
                        brand = "Brand"
                    )
                } else {
                    Sku(
                        upc = upc,
                        name = "",
                        casePack = null,
                        brand = ""
                    )
                }

                /**
                 * Might not want this...
                 */
                val newId = try {
                    skuRepository.upsertAndReturnId(placeHolder)
                } catch (e: Exception) {
                    _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
                    return@launch
                }
                placeHolder.copy(skuId = newId)
            }

            val newItem = AuditItem(
                auditId = auditId,
                count = 0,
                note = null,
                sku = sku
            )


            val currentItems = state.audit.items
            state = state.copy(
                audit = state.audit.copy(
                    items = currentItems + newItem
                )
            )
            try {
                auditItemRepository.insertAuditItem(newItem)
            } catch (e: Exception) {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun addNewItem() {
        val newItem = AuditItem(
            auditId = auditId,
            count = 0,
            note = null,
            sku = Sku(upc = "", name = "", casePack = null, brand = "")
        )

        val currentItems = state.audit.items
        state = state.copy(
            audit = state.audit.copy(
                items = currentItems + newItem
            )
        )

        viewModelScope.launch {
            try {
                auditItemRepository.insertAuditItem(newItem)
                _uiEffect.emit(AuditUiEffect.ShowMessage("New blank audit item added"))
            } catch (e: Exception) {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun getAuditStream(auditId: Long) {
        auditJob?.cancel()
        auditJob = auditRepository
            .getAuditStream(auditId)
            .onStart {
                state = state.copy(isLoading = true, error = null)
            }
            .onEach { audit ->
                audit?.let {
                    state = state.copy(
                        audit = it,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                state = state.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

}