package com.example.merchtools.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.repository.StoreRepository
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

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val auditRepository: AuditRepository,
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var auditJob: Job? = null


    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.StartAuditClicked -> {
                startAudit(event.userName)
            }
            is HomeEvent.OpenAuditClicked -> {
                openAudit()
            }
            is HomeEvent.LoadAudit -> {
                getAuditStream(event.auditId)
            }
        }
    }

    private fun openAudit() {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            val currentAuditId = auditRepository.getCurrentAuditId()
            if(currentAuditId != null) {
                _uiEffect.emit(HomeUiEffect.NavigateToAudit(currentAuditId))
            } else {
                _uiEffect.emit(HomeUiEffect.ShowMessage("No audit in progress"))
            }
        }
    }

    private fun startAudit(userName: String) {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            try {
                val storeId = storeRepository.ensureDefaultStore()
                val newId = auditRepository.startNewAudit(
                    storeId = storeId,
                    createdBy = userName
                )
                _uiEffect.emit(HomeUiEffect.NavigateToAudit(newId))
            } catch (e: Exception) {
                _uiEffect.emit(HomeUiEffect.ShowMessage(e.message ?: "Unknown error"))
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
                state = state.copy(
                    audit = audit,
                    isLoading = false
                )
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