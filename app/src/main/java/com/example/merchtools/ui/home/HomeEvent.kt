package com.example.merchtools.ui.home

sealed class HomeEvent {
    data object StartAuditClicked : HomeEvent()
    data class OnUserNameChanged(val userName: String) : HomeEvent()
    data class OnStoreNameChanged(val storeName: String, val storeId: Long) : HomeEvent()
    data object OpenAuditClicked : HomeEvent()
    data object ShowDialogClicked : HomeEvent()
    data object DismissDialog : HomeEvent()
    data object ExpandStoreMenu : HomeEvent()
    data object CloseStoreMenu : HomeEvent()
}