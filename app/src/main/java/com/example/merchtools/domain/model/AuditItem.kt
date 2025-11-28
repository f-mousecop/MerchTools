package com.example.merchtools.domain.model

data class AuditItem(
    val auditItemId: Long = 0L,
    val auditId: Long,
    val count: Int,
    val note: String?,
    val sku: Sku? = null,
    val photos: List<Photo> = emptyList()
)
