package com.example.merchtools.ui.report

sealed class GenerateReportUiEffect {
    data object GeneratePdf : GenerateReportUiEffect()
    data object NavigateBack : GenerateReportUiEffect()
    data class ShowMessage(val message: String) : GenerateReportUiEffect()
}