package com.example.merchtools.ui.audit

import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.SkuRepository
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
import javax.inject.Inject

@HiltViewModel
class EditAuditItemViewModel @Inject constructor(
    private val auditItemRepository: AuditItemRepository,
    private val skuRepository: SkuRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auditItemId: Long = checkNotNull(savedStateHandle["auditItemId"])
    var state by mutableStateOf(AuditItemState())
        private set

    private val _uiEffect = MutableSharedFlow<AuditItemUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()
    private var auditItemJob: Job? = null

    init {
        getAuditItemStream(auditItemId)
    }

    private fun validate(item: AuditItem): Boolean {
        return item.count >= 0
    }

    fun onEvent(event: AuditItemEvent) {
        when (event) {
            is AuditItemEvent.OnItemChanged -> {
                updateItem(event.item)
            }
            is AuditItemEvent.OnItemFieldChanged -> {
                val updated = updateSku { current ->
                    current.copy(upc = event.userInput)
                }
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.OnNameChanged -> {
                val updated = updateSku { current ->
                    current.copy(name = event.userInput)
                }
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.OnCasePackChanged -> {
                val updated = updateSku { current ->
                    current.copy(casePack = event.userInput.ifBlank { null })
                }
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.OnBrandChanged -> {
                val updated = updateSku { current ->
                    current.copy(brand = event.userInput)
                }
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.OnNoteChanged -> {
                val updated = state.auditItem.copy(note = event.userInput)
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.OnCountChanged -> {
                val updated = state.auditItem.copy(count = event.newCount)
                state = state.copy(
                    auditItem = updated,
                    isEntryValid = validate(updated)
                )
            }
            is AuditItemEvent.AddPhotoToItem -> {
                addPhotoToItem()
            }
            is AuditItemEvent.RemovePhotoFromItem -> {
                removePhotoFromItem()
            }
            is AuditItemEvent.SaveAuditItem -> {
                saveAuditItem()
            }
        }
    }

    /**
     * Might want to see if I can implement insert new SKU and check if user entered
     * a valid UPC (between 12 and 13 digits)
     */
    private fun updateSku(transform: (Sku) -> Sku): AuditItem {
        val currentItem = state.auditItem
        val currentSku = currentItem.sku
            ?: Sku(
                skuId = 0L,
                upc = "",
                name = "",
                casePack = null,
                brand = ""
            )

        return currentItem.copy(
            sku = transform(currentSku)
        )
    }

    private fun removePhotoFromItem() {
        viewModelScope.launch {
            _uiEffect.emit(AuditItemUiEffect.ShowMessage("Not yet implemented"))
        }
    }

    private fun addPhotoToItem() {
        viewModelScope.launch {
            _uiEffect.emit(AuditItemUiEffect.ShowMessage("Not yet implemented"))
        }
    }

    private fun updateItem(item: AuditItem) {
        TODO("Not yet implemented")
    }

    /*private fun updateItemField(userInput: String) {
        state = state.copy(
            auditItem = state.auditItem.copy(
                note = userInput,
                count = userInput.toIntOrNull() ?: 0,
                photos = List<>
            )
        )
    }*/

    private fun saveAuditItem() {
         viewModelScope.launch {
             state = state.copy(isLoading = true)
            try {
                delay(2000L)

                auditItemRepository.updateAuditItem(state.auditItem)

                _uiEffect.emit(AuditItemUiEffect.ShowMessage("Audit item saved"))
                delay(2000L)
                _uiEffect.emit(AuditItemUiEffect.NavigateUp)

            } catch (e: Exception) {
                _uiEffect.emit(AuditItemUiEffect.ShowMessage(e.message ?: "Unknown error"))
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun getAuditItemStream(auditItemId: Long) {
        auditItemJob?.cancel()
        auditItemJob = auditItemRepository
            .getAuditItemStream(auditItemId)
            .onStart {
                state = state.copy(isLoading = true, error = null)
            }
            .onEach { auditItem ->
                println("DEBUG: stream emitted auditItem = $auditItem")
                auditItem?.let {
                    state = state.copy(
                        auditItem = it,
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