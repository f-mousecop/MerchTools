package com.example.merchtools.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.entity.StoreEntity

/**
 * Data class to hold an Audit and associated Store
 * @property audit
 * @property store
 * @constructor AuditWithStore
 */
data class AuditWithStore(
    @Embedded val audit: AuditEntity,
    @Relation(
        parentColumn = "storeId",
        entityColumn = "storeId"
    )
    val store: StoreEntity
)
