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

package com.example.merchtools.ui.audit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.use_case.AddAuditItemUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.core.toDisplayString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AuditViewModel @Inject constructor(
    private val addAuditItemUseCase: AddAuditItemUseCase,
    private val auditItemRepository: AuditItemRepository,
    private val searchSkuUseCase: SearchSkuUseCase,
    private val auditRepository: AuditRepository,
    private val barcodeGenerator: BarcodeGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auditId: Long = checkNotNull(savedStateHandle["auditId"])
    var state by mutableStateOf(AuditState())
        private set

    private val _uiEffect = MutableSharedFlow<AuditUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    val barcodeGen: BarcodeGenerator
        get() = barcodeGenerator

    private var auditJob: Job? = null

    init {
        getAuditStream(auditId)
    }

    fun onEvent(event: AuditEvent) {
        when (event) {
            is AuditEvent.AddNewItem -> {
                addNewItem()
            }
            is AuditEvent.AddItemBySearch -> {
                findSkuByUpc(event.upc)
            }
            is AuditEvent.EditAuditItem -> {
                editAuditItem(event.auditItemId)
            }
            is AuditEvent.BarcodeScanned -> {
                scanBarcode()
            }
            is AuditEvent.OnItemChanged -> {
                updateItem(event.itemIndex, event.item)
            }
            is AuditEvent.RemoveItem -> {
                removeItem(event.item)
            }
            is AuditEvent.SaveAudit -> {
                saveAudit()
            }
            is AuditEvent.DiscardChanges -> {
                discardChanges()
            }
        }
    }

    private fun scanBarcode() {
        viewModelScope.launch {
            _uiEffect.emit(AuditUiEffect.NavigateToScanBarcode)
        }
    }

    private fun saveAudit() {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            try {
                val now = Instant.now()
                val updatedAudit = state.audit.copy(
                    completedAt = now
                )

                state = state.copy(audit = updatedAudit, isLoading = true)
                delay(2000L)

                auditRepository.updateAudit(updatedAudit)

                state = state.copy(isLoading = false)
                _uiEffect.emit(AuditUiEffect.ShowMessage("Audit saved at: ${now.toDisplayString()}"))
                delay(2000L)
                _uiEffect.emit(AuditUiEffect.NavigateToHistoryScreen)
            } catch (e: Exception) {
                state = state.copy(isLoading = false)
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun discardChanges() {
        TODO("Not yet implemented")
    }

    private fun editAuditItem(auditItemId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(AuditUiEffect.NavigateToEditAuditItem(auditItemId))
        }
    }

    private fun removeItem(item: AuditItem) {
        val currentItems = state.audit.items.toMutableList()
        viewModelScope.launch {
            try {
                state = state.copy(
                    audit = state.audit.copy(
                        items = currentItems
                    )
                )

                auditItemRepository.deleteAuditItem(item)
                _uiEffect.emit(AuditUiEffect.ShowMessage("Audit item removed"))
            } catch (e: Exception) {
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
            try {
                val sku = searchSkuUseCase.byUpc(upc)

                if (sku == null) {
                    _uiEffect.emit(
                        AuditUiEffect.ShowMessage("No SKU found for UPC: $upc"))
                    return@launch
                }

                val newItem = addAuditItemUseCase(auditId, sku)

                val currentItems = state.audit.items
                state = state.copy(
                    audit = state.audit.copy(
                        items = currentItems + newItem
                    )
                )
                _uiEffect.emit(
                    AuditUiEffect.ShowMessage("New audit item added with UPC: $upc")
                )
            } catch (e: Exception) {
                _uiEffect.emit(
                    AuditUiEffect.ShowMessage(e.message ?: "Unknown error")
                )
            }
        }
    }

    private fun addNewItem() {
        viewModelScope.launch {
            try {
                _uiEffect.emit(
                    AuditUiEffect.ShowMessage("Enter a valid UPC to add audit item")
                )
            } catch (e: Exception) {
                _uiEffect.emit(
                    AuditUiEffect.ShowMessage(e.message ?: "Unknown error")
                )
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