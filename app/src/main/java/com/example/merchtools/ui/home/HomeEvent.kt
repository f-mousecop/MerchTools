package com.example.merchtools.ui.home

sealed class HomeEvent {
    data object StartAuditClicked : HomeEvent()
    data object OpenAuditClicked : HomeEvent()
    data class LoadAudit(val auditId: Long): HomeEvent()
}