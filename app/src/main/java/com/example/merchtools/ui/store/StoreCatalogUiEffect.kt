package com.example.merchtools.ui.store

sealed class StoreCatalogUiEffect {
    data object NavigateToEditStore : StoreCatalogUiEffect()
    data class ShowMessage(val message: String) : StoreCatalogUiEffect()
}