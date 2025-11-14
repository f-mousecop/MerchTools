package com.example.merchtools.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for Audit Item object
@Entity(tableName = "audit_items",
    foreignKeys = [
        ForeignKey(
            entity = AuditEntity::class,
            parentColumns = ["auditId"],
            childColumns = ["auditId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SkuEntity::class,
            parentColumns = ["skuId"],
            childColumns = ["skuId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class AuditItemEntity(
    @PrimaryKey(autoGenerate = true) val auditItemId: Long = 0,
    val auditId: Long,
    val skuId: Long,
    val count: Int,
    val note: String?
)