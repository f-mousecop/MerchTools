package com.example.merchtools.ui.home

sealed class HomeUiEffect {
    data class NavigateToAudit(val auditId: Long) : HomeUiEffect()
    data class ShowMessage(val message: String) : HomeUiEffect()
}