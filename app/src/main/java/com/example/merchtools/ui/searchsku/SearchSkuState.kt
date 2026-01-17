package com.example.merchtools.ui.searchsku

import com.example.merchtools.domain.model.Sku

data class SearchSkuState(
    val skus: List<Sku> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null
)
