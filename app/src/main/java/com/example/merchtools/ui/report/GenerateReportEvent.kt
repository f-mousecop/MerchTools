package com.example.merchtools.ui.report

sealed class GenerateReportEvent {
    data object GeneratePdfClicked : GenerateReportEvent()
    data object NavigateBack : GenerateReportEvent()
}