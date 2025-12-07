package com.example.merchtools.ui.history

import com.example.merchtools.domain.model.Audit

sealed class HistoryEvent {
    data class OpenAuditClicked(val auditId: Long) : HistoryEvent()
    data class DeleteAudit(val audit: Audit) : HistoryEvent()
    data class ExportPdfClicked(val auditId: Long) : HistoryEvent()
}