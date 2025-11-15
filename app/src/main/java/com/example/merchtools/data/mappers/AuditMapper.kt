package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.relations.AuditWithItems
import com.example.merchtools.domain.model.Audit

fun AuditEntity.toAudit(): Audit {
    return Audit(
        storeId = storeId,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy
    )
}

fun Audit.toAuditEntity(): AuditEntity {
    return AuditEntity(
        storeId = storeId,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy
    )
}

fun AuditWithItems.toDomain(): Audit {
    return audit.toAudit().copy(
        items = this.items.map { it.toAuditItem() }
    )
}