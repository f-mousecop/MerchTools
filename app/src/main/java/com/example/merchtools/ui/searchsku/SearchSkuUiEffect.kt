package com.example.merchtools.ui.searchsku

sealed class SearchSkuUiEffect {
    data object NavigateUp : SearchSkuUiEffect()
    data class NavigateToSkuDetails(val skuId: Long) : SearchSkuUiEffect()
    data object NavigateToScanBarcode : SearchSkuUiEffect()
    data class ShowMessage(val message: String) : SearchSkuUiEffect()
}