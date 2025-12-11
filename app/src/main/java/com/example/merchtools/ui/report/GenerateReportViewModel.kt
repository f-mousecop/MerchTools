package com.example.merchtools.ui.report

import android.content.Context
import android.net.Uri
import android.print.PrintDocumentAdapter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.domain.use_case.AuditReportHtmlBuilder
import com.example.merchtools.domain.use_case.GenerateReportUseCase
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.util.AuditReportPrintAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateReportViewModel @Inject constructor(
    private val auditRepository: AuditRepository,
    private val barcodeGenerator: BarcodeGenerator,
    private val generateReportUseCase: GenerateReportUseCase,
    private val auditReportHtmlBuilder: AuditReportHtmlBuilder,
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
                generatePdf()
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

    fun buildPrintAdapter(
        context: Context,
        onResult: (Result<Uri>) -> Unit
    ): PrintDocumentAdapter {
        val data = state.report
        return AuditReportPrintAdapter(context, data, barcodeGenerator, onResult)
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(GenerateReportUiEffect.NavigateBack)
        }
    }


    private fun generatePdf() {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)
                prepareReport()
                _uiEffect.emit(GenerateReportUiEffect.GeneratePdf)
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message
                )
                _uiEffect.emit(
                    GenerateReportUiEffect.ShowMessage(
                        e.message ?: "Unknown error"
                    )
                )
            } finally {
                state = state.copy(
                    isLoading = false
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