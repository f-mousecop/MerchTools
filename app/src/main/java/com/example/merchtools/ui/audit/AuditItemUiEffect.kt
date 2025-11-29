package com.example.merchtools.ui.audit

sealed class AuditItemUiEffect {
    data object NavigateUp : AuditItemUiEffect()
    data class ShowMessage(val message: String) : AuditItemUiEffect()
}