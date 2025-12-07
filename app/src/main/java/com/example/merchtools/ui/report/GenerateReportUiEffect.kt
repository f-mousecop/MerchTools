package com.example.merchtools.ui.report

sealed class GenerateReportUiEffect {
    data class GeneratePdf(val html: String) : GenerateReportUiEffect()
    data object NavigateBack : GenerateReportUiEffect()
    data class ShowMessage(val message: String) : GenerateReportUiEffect()
}