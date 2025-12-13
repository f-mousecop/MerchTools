package com.example.merchtools.ui.report

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.use_case.GenerateReportUseCase
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.core.AuditReportPdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GenerateReportViewModel @Inject constructor(
    private val auditRepository: AuditRepository,
    private val barcodeGenerator: BarcodeGenerator,
    private val generateReportUseCase: GenerateReportUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val auditId: Long = checkNotNull(savedStateHandle["auditId"])
    var state by mutableStateOf(GenerateReportState())
        private set

    private val _uiEffect = MutableSharedFlow<GenerateReportUiEffect>()
    val uiEffect = _uiEffect
    val barcodeGen: BarcodeGenerator
        get() = barcodeGenerator

    private var auditJob: Job? = null
    init {
        getAuditStream(auditId)
    }

    fun onEvent(event: GenerateReportEvent) {
        when (event) {
            is GenerateReportEvent.GeneratePdfClicked -> {
                generatePdf(event.context, event.onResult)
            }
            is GenerateReportEvent.NavigateBack -> {
                navigateBack()
            }
        }
    }

    fun prepareReport() {
        state = state.copy(
            report = generateReportUseCase(state.audit)
        )
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(GenerateReportUiEffect.NavigateBack)
        }
    }


    private fun generatePdf(
        context: Context,
        onResult: (Result<Uri>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                state = state.copy(isLoading = true, error = null)
                prepareReport()

                val report = state.report
                if (report == null) {
                    withContext(Dispatchers.Main) {
                        val failure = onResult(Result.failure(IllegalStateException("Report not ready")))
                        _uiEffect.emit(GenerateReportUiEffect.ShowMessage(failure.toString()))
                    }
                    return@launch
                }
                delay(2000)
                _uiEffect.emit(GenerateReportUiEffect.ShowMessage("Generating report..."))
                delay(2000)

                val result = AuditReportPdfGenerator(
                    context,
                    report,
                    barcodeGenerator
                ).generateReport()

                withContext(Dispatchers.Main) {
                    onResult(result)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(Result.failure(e))
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                    _uiEffect.emit(
                        GenerateReportUiEffect.ShowMessage(
                            e.message ?: "Unknown error"
                        )
                    )
                }
            } finally {
                withContext(Dispatchers.Main) {
                    state = state.copy(
                        isLoading = false
                    )
                }
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