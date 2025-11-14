package com.example.merchtools.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for Audit object
@Entity(tableName = "audits",
    foreignKeys = [
        ForeignKey(
            StoreEntity::class,
            parentColumns = ["storeId"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class AuditEntity(
    @PrimaryKey(autoGenerate = true) val auditId: Long = 0,
    val storeId: Long,
    val startedAt: Long,
    val completedAt: Long,
    val createdBy: String?
)