package com.example.merchtools.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.AuditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val auditRepository: AuditRepository
) : ViewModel() {
    var state by mutableStateOf(HistoryState())
        private set

    private var historyJob: Job? = null

    init {
//        getAllAuditsStream()
    }

    /*private fun getAllAuditsStream() {
        historyJob?.cancel()
        historyJob = auditRepository
            .getAllAuditsStream()
            .onStart {
                state = state.copy(isLoading = true, error = null)
            }.onEach { audits ->
                audits?.let {
                    state = state.copy(
                        history = state.copy(
                            history = state.copy(
                                history = audits,
                                isLoading = false
                            )
                        )
                    )
                }
            }.catch { e ->
                state = state.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }.launchIn(viewModelScope)
    }*/
}