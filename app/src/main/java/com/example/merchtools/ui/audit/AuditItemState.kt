package com.example.merchtools.ui.audit

import com.example.merchtools.domain.model.AuditItem

data class AuditItemState (
    val auditItem: AuditItem = AuditItem(),
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)