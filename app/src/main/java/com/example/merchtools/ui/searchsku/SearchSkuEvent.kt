package com.example.merchtools.ui.searchsku

import com.example.merchtools.domain.model.Sku

sealed class SearchSkuEvent {
    data object BarcodeScanned : SearchSkuEvent()
    data class AddNewSku(val upc: String) : SearchSkuEvent()
    data class EditSku(val skuId: Long) : SearchSkuEvent()
    data class RemoveSku(val sku: Sku) : SearchSkuEvent()
    data class OnSearchQueryChange(val query: String): SearchSkuEvent()
}