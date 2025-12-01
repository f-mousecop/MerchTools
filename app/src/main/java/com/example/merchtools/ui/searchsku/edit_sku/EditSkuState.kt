package com.example.merchtools.ui.searchsku.edit_sku

import com.example.merchtools.domain.model.Sku

data class EditSkuState(
    val sku: Sku = Sku(),
    val isEntryValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
