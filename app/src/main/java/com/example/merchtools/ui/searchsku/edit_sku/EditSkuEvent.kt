package com.example.merchtools.ui.searchsku.edit_sku

sealed class EditSkuEvent {
    data class OnUpcChanged(val userInput: String) : EditSkuEvent()
    data class OnNameChanged(val userInput: String) : EditSkuEvent()
    data class OnCasePackChanged(val userInput: String) : EditSkuEvent()
    data class OnBrandChanged(val userInput: String) : EditSkuEvent()
    data object SaveSku : EditSkuEvent()
}