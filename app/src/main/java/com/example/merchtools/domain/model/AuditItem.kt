package com.example.merchtools.domain.model

data class AuditItem(
    val auditId: Long,
    val skuId: Long,
    val count: Int,
    val note: String?,
    val sku: Sku? = null,
    val photos: List<Photo> = emptyList()
)
