package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.relations.AuditItemWithSkuAndPhoto
import com.example.merchtools.domain.model.AuditItem

fun AuditItemWithSkuAndPhoto.toAuditItem(): AuditItem {
    return AuditItem(
        // Data from the embedded auditItem
        auditId = this.auditItem.auditId,
        skuId = this.auditItem.skuId,
        count = this.auditItem.count,
        note = this.auditItem.note,

        // Data from the related SkuEntity mapped to a domain Sku
        sku = this.sku.toSku(),

        // Data from the related PhotoEntities mapped to a list of domain Photos
        photos = this.photos.map { it.toPhoto() }
    )
}

fun AuditItemEntity.toDomain(): AuditItem {
    return AuditItem(
        auditId = this.auditId,
        skuId = this.skuId,
        count = this.count,
        note = this.note
    )
}

fun AuditItem.toAuditItemEntity(): AuditItemEntity {
    return AuditItemEntity(
        auditId = this.auditId,
        skuId = this.skuId,
        count = this.count,
        note = this.note
    )
}