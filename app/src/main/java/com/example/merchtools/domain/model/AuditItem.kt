package com.example.merchtools.domain.model

data class AuditItem(
    val auditItemId: Long = 0L,
    val auditId: Long = 0L,
    val count: Int = 0,
    val note: String? = null,
    val sku: Sku? = null,
    val photos: List<Photo> = emptyList()
)
