package com.example.merchtools.ui.store

import com.example.merchtools.domain.model.Store

data class StoreState(
    val stores: List<Store> = emptyList(),
    val isAddStoreDialogOpen: Boolean = false,
    val newStoreName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
