package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.relations.AuditWithItems
import com.example.merchtools.data.local.relations.AuditWithStore
import com.example.merchtools.domain.model.Audit

fun AuditEntity.toAudit(): Audit {
    return Audit(
        auditId = auditId,
        storeId = storeId,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy,
    )
}

fun Audit.toAuditEntity(): AuditEntity {
    return AuditEntity(
        auditId = auditId,
        storeId = storeId,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy
    )
}

fun AuditWithItems.toDomain(): Audit {
    return audit.toAudit().copy(
        items = this.items.map { it.toAuditItem() },
        store = this.store.toStore()
    )
}