package com.example.merchtools.ui.home

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Store

data class HomeState(
    val audit: Audit? = null,
    val stores: List<Store> = emptyList(),
    val userName: String = "",
    val storeName: String = "",
    val storeId: Long = 0L,
    val isExpanded: Boolean = false,
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
