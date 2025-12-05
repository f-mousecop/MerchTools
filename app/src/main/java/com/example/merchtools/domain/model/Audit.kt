package com.example.merchtools.domain.model

import java.time.Instant

data class Audit(
    val auditId:Long = 0L,
    val storeId: Long = 0L,
    val startedAt: Instant? = null,
    val completedAt: Instant? = null,
    val createdBy: String? = null,
    val items: List<AuditItem> = emptyList()
)
