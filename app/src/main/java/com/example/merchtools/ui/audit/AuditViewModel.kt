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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.merchtools.core.Resource
import com.example.merchtools.core.toDisplayString
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.use_case.AddAuditItemUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import com.example.merchtools.domain.util.BarcodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _uiEffect = MutableSharedFlow<AuditUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    val barcodeGen: BarcodeGenerator
        get() = barcodeGenerator


    /**
     * Represents the current state of the audit screen, observed as a [StateFlow].
     * This flow is derived from the audit repository and reflects real-time updates
     * to the audit data.
     *
     * It starts with an initial [AuditState] indicating a loading status.
     * On successfully fetching the audit, it emits a new state with the audit data.
     * If the audit is not found or an error occurs during fetching, it emits a state
     * containing an appropriate error message.
     *
     * The flow is managed within the [viewModelScope] and is configured to stay active
     * for 5 seconds after the last observer unsubscribes ([SharingStarted.WhileSubscribed]).
     */
    val uiState: StateFlow<AuditState> = auditRepository
        .getAuditStream(auditId)
        .map { audit ->
            if (audit == null) AuditState(error = "Audit not found")
            else AuditState(audit = audit, isLoading = false)
        }
        .catch { e ->
            emit(AuditState(error = e.message ?: "Unknown error", isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuditState(isLoading = true)
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _expanded = MutableStateFlow(false)
    val expanded = _expanded.asStateFlow()

    // Flag to track if the last query change was from a selection
    private var isSelectionUpdate = false

    /**
     * A [StateFlow] that holds the list of [Sku]s matching the current search query.
     *
     * This flow reacts to changes in the [searchQuery] flow. It debounces the input
     * to avoid excessive network requests, ensures the query has actually changed,
     * and then uses the [searchSkuUseCase] to fetch the results from the catalog.
     *
     * - If the query is blank or the change was triggered by selecting an item from the
     *   search results (indicated by `isSelectionUpdate`), it emits an empty list.
     * - On a successful search, it updates the list with the fetched [Sku]s and sets
     *   the [_expanded] state to `true` to show the results dropdown.
     * - In case of an error or loading state, it emits an empty list without changing the
     *   dropdown's expanded state to prevent UI flickering.
     *
     * The results are collected as a [StateFlow] to be observed by the UI, with a `WhileSubscribed`
     * sharing strategy to manage the lifecycle of the upstream flow efficiently.
     */
    val searchResults = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank() || isSelectionUpdate) {
                isSelectionUpdate = false
                flowOf(emptyList<Sku>())
            } else {
                searchSkuUseCase.catalog(query).map { result ->
                    when (result) {
                        is Resource.Loading -> emptyList()
                        is Resource.Success -> {
                            _expanded.value = true
                            result.data.orEmpty()
                        }
                        is Resource.Error -> {
                            // Keep expanded state as-is on error to avoid flickering
                            emptyList()
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onEvent(event: AuditEvent) {
        when (event) {
            is AuditEvent.AddItemBySearch -> findSkuByUpc(event.upc)
            is AuditEvent.EditAuditItem -> editAuditItem(event.auditItemId)
            is AuditEvent.BarcodeScanned -> scanBarcode()
            is AuditEvent.RemoveItem -> removeItem(event.item)
            is AuditEvent.SaveAudit -> saveAudit()
            is AuditEvent.DiscardChanges -> {} // TODO
            is AuditEvent.OnSearchQueryChanged -> {
                isSelectionUpdate = event.fromSelection
                _searchQuery.value = event.query

                if (event.fromSelection) {
                    isSelectionUpdate = true
                    _expanded.value = false
                }
            }
            is AuditEvent.OnSearchExpandedChanged -> {
                if (!event.expanded) {
                    _expanded.value = false
                }
            }
        }
    }

    private fun scanBarcode() {
        viewModelScope.launch {
            _uiEffect.emit(AuditUiEffect.NavigateToScanBarcode)
        }
    }

    /**
     * Finalizes and saves the current audit by updating its completion timestamp.
     *
     * This function retrieves the current audit state, updates the `completedAt` field with the
     * current time, and persists the changes to the repository. Upon successful completion,
     * it triggers a UI message and navigates the user back to the history screen.
     *
     * In case of a database error, an error message is emitted via the UI effect flow.
     */
    private fun saveAudit() {
        val currentAudit = uiState.value.audit ?: return
        viewModelScope.launch {
            try {
                val now = Instant.now()
                auditRepository.updateAudit(currentAudit.copy(completedAt = now))

                _uiEffect.emit(AuditUiEffect.ShowMessage("Audit saved at: ${now.toDisplayString()}"))
                delay(2000L)
                _uiEffect.emit(AuditUiEffect.NavigateToHistoryScreen)
            } catch (e: SQLiteException) {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun editAuditItem(auditItemId: Long) {
        viewModelScope.launch {
            _uiEffect.emit(AuditUiEffect.NavigateToEditAuditItem(auditItemId))
        }
    }

    private fun removeItem(item: AuditItem) {
        viewModelScope.launch {
            try {
                auditItemRepository.deleteAuditItem(item)
                _uiEffect.emit(AuditUiEffect.ShowMessage("Audit item removed"))
            } catch (e: Exception) {
                _uiEffect.emit(AuditUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    /**
     * Searches for a SKU by its UPC and adds it to the current audit if found.
     *
     * This function performs validation to ensure the UPC is not blank and that the item
     * does not already exist in the current audit list. If a matching SKU is found and
     * is unique to the audit, it persists the new audit item and provides UI feedback.
     *
     * @param upc The Universal Product Code string to search for.
     */
    private fun findSkuByUpc(upc: String) {
        viewModelScope.launch {
            if (upc.isBlank()) {
                _uiEffect.emit(AuditUiEffect.ShowMessage("Enter a valid UPC"))
                return@launch
            }
            val sku = searchSkuUseCase.byUpc(upc)
            if (sku == null) {
                _uiEffect.emit(AuditUiEffect.ShowMessage("No SKU found for UPC: $upc"))
                return@launch
            }

            if (uiState.value.audit.items.any { it.sku?.upc == sku.upc }) {
                _uiEffect.emit(AuditUiEffect.ShowMessage("Item: ${sku.brand} ${sku.casePack} already in audit"))
                return@launch
            }
            addAuditItemUseCase(auditId, sku)
            _uiEffect.emit(AuditUiEffect.ShowMessage("New item added: ${sku.brand} ${sku.casePack}"))
        }
    }
}
