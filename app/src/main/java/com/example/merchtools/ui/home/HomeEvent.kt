package com.example.merchtools.ui.home

sealed class HomeEvent {
    data class StartAuditClicked(val userName: String) : HomeEvent()
    data object OpenAuditClicked : HomeEvent()
    data class LoadAudit(val auditId: Long): HomeEvent()
}