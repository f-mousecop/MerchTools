package com.example.merchtools.domain.model

data class Audit(
    val storeId: Long,
    val startedAt: Long,
    val completedAt: Long,
    val createdBy: String?,
    val items: List<AuditItem> = emptyList()
)
