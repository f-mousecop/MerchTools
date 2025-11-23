package com.example.merchtools.domain.model

data class Audit(
    val auditId:Long = 0L,
    val storeId: Long = 0L,
    val startedAt: Long = 0L,
    val completedAt: Long = 0L,
    val createdBy: String? = null,
    val items: List<AuditItem> = emptyList()
)
