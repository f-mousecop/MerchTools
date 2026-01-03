package com.example.merchtools.ui.searchsku

sealed class SearchSkuUiEffect {
    object NavigateUp : SearchSkuUiEffect()
    data class NavigateToSkuDetails(val skuId: Long) : SearchSkuUiEffect()
    object NavigateToScanBarcode : SearchSkuUiEffect()
    data class ShowMessage(val message: String) : SearchSkuUiEffect()
}