package com.example.merchtools.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.core.Resource
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.repository.StoreRepository
import com.example.merchtools.domain.validation.TextInputFieldValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private var storeJob: Job? = null

    init {
        getAllStoresStream()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.StartAuditClicked -> {
                state = state.copy(showDialog = false)
                startAudit(state.userName, state.storeId)
            }
            is HomeEvent.OnUserNameChanged -> {
                state = state.copy(
                    userName = TextInputFieldValidator.capInputLength(event.userName)
                )
            }
            is HomeEvent.OnStoreNameChanged -> {
                state = state.copy(
                    storeName = event.storeName,
                    storeId = event.storeId
                )
            }
            is HomeEvent.OpenAuditClicked -> {
                openAudit()
            }
            is HomeEvent.ShowDialogClicked -> {
                state = state.copy(showDialog = true)
            }
            is HomeEvent.DismissDialog -> {
                state = state.copy( showDialog = false)
                clearFields()
            }
            is HomeEvent.ExpandStoreMenu -> {
                state = state.copy(isExpanded = true)
            }
            is HomeEvent.CloseStoreMenu -> {
                state = state.copy(isExpanded = false)
            }
        }
    }

    private fun openAudit() {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            val currentAuditId = auditRepository.getCurrentAuditId()
            if (currentAuditId != null) {
                _uiEffect.emit(HomeUiEffect.NavigateToAudit(currentAuditId))
            } else {
                _uiEffect.emit(HomeUiEffect.ShowMessage("No audit in progress"))
            }
        }
    }

    // Need to reset state of userName, storeName, storeId upon
    // dismissing dialog or navigating
    private fun clearFields() {
        state = state.copy(
            userName = "",
            storeName = "",
            storeId = 0L
        )
    }

    private fun startAudit(userName: String, storeId: Long) {
        // Trim trailing spaces from the user name before inserting it into the database
        val createdBy = TextInputFieldValidator.trimTrailingSpaces(userName)

        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            try {
                val newId = auditRepository.startNewAudit(
                    storeId = storeId,
                    createdBy = createdBy
                )
                _uiEffect.emit(HomeUiEffect.NavigateToAudit(newId))

                clearFields()

            } catch (e: Exception) {
                _uiEffect.emit(HomeUiEffect.ShowMessage(e.message ?: "Unknown error"))
            }

        }
    }

    private fun getAllStoresStream() {
        storeJob?.cancel()
        storeJob = viewModelScope.launch {
            storeRepository
                .getAllStoresStream()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                state = state.copy(
                                    stores = it,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}