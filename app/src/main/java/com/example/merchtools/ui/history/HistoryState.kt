package com.example.merchtools.ui.history

import com.example.merchtools.domain.model.Audit

data class HistoryState(
    val history: List<Audit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)