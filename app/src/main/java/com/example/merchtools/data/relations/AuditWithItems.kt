package com.example.merchtools.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.merchtools.data.entity.AuditEntity
import com.example.merchtools.data.entity.AuditItemEntity
import com.example.merchtools.data.entity.SkuEntity

/**
 * Data class to hold audit item and relevant fields.
 * We want to get the [AuditItemEntity] associated with the [SkuEntity], as well as the Photo
 * @property auditItem
 * @property sku
 * @property photos
 * @constructor AuditItemWithSkuAndPhoto
 */
data class AuditItemWithSkuAndPhoto(
    @Embedded val auditItem: AuditItemEntity,
    @Relation(
        parentColumn = "skuId",
        entityColumn = "skuId"
    )
    val sku: SkuEntity,
    @Relation(
        parentColumn = "auditItemId",
        entityColumn = "auditItemId"
    )
    val photos: List<com.example.merchtools.data.entity.PhotoEntity>
)

/**
 * Data class to hold [AuditEntity] and associated Audit Items
 * @property audit
 * @property items
 * @constructor AuditWithItems
 * @see AuditItemWithSkuAndPhoto
 */
data class AuditWithItems(
    @Embedded val audit: AuditEntity,
    @Relation(
        entity = AuditItemEntity::class,
        parentColumn = "auditId",
        entityColumn = "auditId"
    )
    val items: List<AuditItemWithSkuAndPhoto>
)
