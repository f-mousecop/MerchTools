package com.example.merchtools.ui.audit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.AuditItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAuditItemViewModel @Inject constructor(
    private val auditItemRepository: AuditItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auditItemId: Long = checkNotNull(savedStateHandle["auditItemId"])
    var state by mutableStateOf(AuditItemState())
        private set

    private var auditItemJob: Job? = null

    init {
        viewModelScope.launch {
            getAuditItemStream(auditItemId)
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