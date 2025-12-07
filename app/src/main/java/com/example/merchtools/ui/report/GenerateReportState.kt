package com.example.merchtools.ui.report

import com.example.merchtools.domain.model.Audit

data class GenerateReportState(
    val audit: Audit = Audit(),
    val isLoading: Boolean = false,
    val error: String? = null
)
