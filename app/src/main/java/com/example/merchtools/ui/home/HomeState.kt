package com.example.merchtools.ui.home

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Store

data class HomeState(
    val audit: Audit? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
