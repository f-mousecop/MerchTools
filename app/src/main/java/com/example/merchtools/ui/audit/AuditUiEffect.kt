package com.example.merchtools.ui.audit

sealed class AuditUiEffect {
    data class NavigateToEditAuditItem(val auditItemId: Long) : AuditUiEffect()
    data object NavigateToHistoryScreen : AuditUiEffect()
    data object NavigateToScanBarcode : AuditUiEffect()
    data class ShowMessage(val message: String) : AuditUiEffect()
}