package com.example.merchtools.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val auditRepository: AuditRepository
) : ViewModel() {
    var state by mutableStateOf(HistoryState())
        private set

    private val _uiEffect = MutableSharedFlow<HistoryUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()


    private var historyJob: Job? = null

    init {
        getAllAuditsStream()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OpenAuditClicked -> {
                openAudit(event.auditId)
            }
            is HistoryEvent.DeleteAudit -> {
                deleteAudit(event.audit)
            }
            is HistoryEvent.ExportPdfClicked -> {
                exportPdf(event.auditId)
            }
        }
    }

    private fun deleteAudit(audit: Audit) {
        val currentItems = state.audits.toMutableList()
        viewModelScope.launch {
            try {
                state = state.copy(
                    audits = currentItems
                )

                auditRepository.deleteAudit(audit)
                _uiEffect.emit(HistoryUiEffect.ShowMessage(
                    "Audit ${audit.store?.name} created by ${audit.createdBy} deleted")
                )
            } catch (e: Exception) {
                _uiEffect.emit(HistoryUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun exportPdf(auditId: Long) {
        viewModelScope.launch {
            try {
                _uiEffect.emit(HistoryUiEffect.NavigateToReportScreen(auditId))
            } catch (e: Exception) {
                _uiEffect.emit(HistoryUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun openAudit(auditId: Long) {
        viewModelScope.launch {
            try {
                _uiEffect.emit(HistoryUiEffect.NavigateToAudit(auditId))
            } catch (e: Exception) {
                _uiEffect.emit(HistoryUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }
        }
    }

    private fun getAllAuditsStream() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            auditRepository
                .getAllAuditsStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                state = state.copy(
                                    audits = it
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(error = result.message)
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}