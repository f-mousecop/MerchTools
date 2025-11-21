package com.example.merchtools.ui.searchsku

sealed class SearchSkuEvent {
    object Refresh: SearchSkuEvent()
    data class OnSearchQueryChange(val query: String): SearchSkuEvent()
}