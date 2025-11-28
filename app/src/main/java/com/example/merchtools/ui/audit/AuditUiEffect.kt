package com.example.merchtools.ui.audit

sealed class AuditUiEffect {
    data class ShowMessage(val message: String) : AuditUiEffect()
}