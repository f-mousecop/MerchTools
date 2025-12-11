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
    val storeName: String = "",
    val createdBy: String = "",
    val startedAt: String = "",
    val completedAt: String = "",
    val items: List<ReportItemRow> = emptyList()
)
