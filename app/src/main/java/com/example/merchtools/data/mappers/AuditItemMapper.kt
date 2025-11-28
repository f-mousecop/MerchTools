package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.relations.AuditItemWithSkuAndPhoto
import com.example.merchtools.domain.model.AuditItem

fun AuditItemWithSkuAndPhoto.toAuditItem(): AuditItem {
    return AuditItem(
        // Data from the embedded auditItem
        auditItemId = auditItem.auditItemId,
        auditId = auditItem.auditId,
        count = auditItem.count,
        note = auditItem.note,

        // Data from the related SkuEntity mapped to a domain Sku
        sku = sku?.toSku(),

        // Data from the related PhotoEntities mapped to a list of domain Photos
        photos = photos.map { it.toPhoto() }
    )
}

fun AuditItemEntity.toDomain(): AuditItem {
    return AuditItem(
        auditItemId = auditItemId,
        auditId = auditId,
        count = count,
        note = note
    )
}

fun AuditItem.toAuditItemEntity(): AuditItemEntity {
    return AuditItemEntity(
        auditItemId = auditItemId,
        auditId = auditId,
        skuId = sku?.skuId?.takeIf { it != 0L },
        count = count,
        note = note
    )
}