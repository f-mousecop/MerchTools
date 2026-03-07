package com.example.merchtools.ui.audit.edit_audit_item

import android.database.sqlite.SQLiteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.SnackbarAction
import com.example.merchtools.SnackbarController
import com.example.merchtools.SnackbarEvent
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.AuditItemRepository
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.domain.validation.AuditItemValidator
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
    private val barcodeGenerator: BarcodeGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auditItemId: Long = checkNotNull(savedStateHandle["auditItemId"])
    var state by mutableStateOf(AuditItemState())
        private set

    private val _uiEffect = MutableSharedFlow<AuditItemUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    val barcodeGen: BarcodeGenerator
        get() = barcodeGenerator

    private var auditItemJob: Job? = null

    init {
        getAuditItemStream(auditItemId)
    }


    fun onEvent(event: AuditItemEvent) {
        when (event) {
            is AuditItemEvent.OnNoteChanged -> {
                state = state.copy(
                    auditItem = state.auditItem.copy(
                        note = AuditItemValidator.capInputLength(event.userInput)
                    )
                )
            }
            is AuditItemEvent.OnCountChanged -> {
                state = state.copy(
                    auditItem = state.auditItem.copy(count = event.newCount)
                )
                state = state.copy(
                    isEntryValid = AuditItemValidator.isValidCount(event.newCount)
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

    private fun saveAuditItem() {
         viewModelScope.launch {
             state = state.copy(isLoading = true)
             val cleanedNote = AuditItemValidator.trimTrailingSpaces(
                 state.auditItem.note.orEmpty()
             )
             state = state.copy(
                 auditItem = state.auditItem.copy(note = cleanedNote)
             )
            try {
                auditItemRepository.updateAuditItem(state.auditItem)

                _uiEffect.emit(AuditItemUiEffect.ShowMessage("Audit item saved"))
                _uiEffect.emit(AuditItemUiEffect.NavigateUp)

            } catch (e: SQLiteException) {
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