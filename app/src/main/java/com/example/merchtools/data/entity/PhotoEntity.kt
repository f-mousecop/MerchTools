package com.example.merchtools.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for Photo object (attached to Audit Item)
@Entity(tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = AuditItemEntity::class,
            parentColumns = ["auditItemId"],
            childColumns = ["auditItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val photoId: Long = 0,
    val auditItemId: Long,
    val uri: String,
    val createdAt: Long
)