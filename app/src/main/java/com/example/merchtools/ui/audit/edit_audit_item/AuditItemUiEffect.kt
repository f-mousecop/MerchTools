package com.example.merchtools.ui.audit.edit_audit_item

sealed class AuditItemUiEffect {
    data object NavigateUp : AuditItemUiEffect()
    data class ShowMessage(val message: String) : AuditItemUiEffect()
}