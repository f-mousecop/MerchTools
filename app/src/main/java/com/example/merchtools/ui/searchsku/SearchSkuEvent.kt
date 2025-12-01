package com.example.merchtools.ui.searchsku

sealed class SearchSkuEvent {
    object Refresh: SearchSkuEvent()
    data object AddNewSku : SearchSkuEvent()
    data class EditSku(val skuId: Long) : SearchSkuEvent()
    data class OnSearchQueryChange(val query: String): SearchSkuEvent()
}