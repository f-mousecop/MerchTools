package com.example.merchtools.domain.model

data class ReportItemRow(
    val index: Int,
    val upc: String,
    val skuName: String,
    val brandInfo: String,
    val count: Int,
    val note: String
)

data class Report(
    val auditId: Long = 0L,
    val storeName: String? = null,
    val createdBy: String? = null,
    val startedAt: String? = null,
    val completedAt: String? = null,
    val items: List<ReportItemRow> = emptyList()
)
