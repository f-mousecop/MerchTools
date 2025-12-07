package com.example.merchtools.ui.history

sealed class HistoryUiEffect {
    data class NavigateToAudit(val auditId: Long) : HistoryUiEffect()
    data class NavigateToReportScreen(val auditId: Long) : HistoryUiEffect()
    data class ShowMessage(val message: String) : HistoryUiEffect()
}