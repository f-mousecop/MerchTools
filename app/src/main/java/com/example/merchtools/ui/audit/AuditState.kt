package com.example.merchtools.ui.audit

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.model.Store

data class AuditState(
    val audit: Audit = Audit(),
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
