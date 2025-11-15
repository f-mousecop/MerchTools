package com.example.merchtools.ui.home

import com.example.merchtools.domain.model.Store

data class HomeState(
    val stores: List<Store> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
