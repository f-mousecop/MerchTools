package com.example.merchtools.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.entity.PhotoEntity
import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.data.local.entity.StoreEntity

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
    val sku: SkuEntity?,
    @Relation(
        parentColumn = "auditItemId",
        entityColumn = "auditItemId"
    )
    val photos: List<PhotoEntity>
)

/**
 * Data class to hold [AuditEntity] and associated Audit Items
 * @property audit
 * @property items
 * @property store
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
    val items: List<AuditItemWithSkuAndPhoto>,

    @Relation(
        parentColumn = "storeId",
        entityColumn = "storeId"
    )
    val store: StoreEntity
)
