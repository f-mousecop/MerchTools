package com.example.merchtools.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.entity.PhotoEntity

/**
 * This class captures the relationship between an [AuditItemEntity] and a list of 0 or more attached Photos
 * used by Room to fetch the related data
 *
 * @see AuditItemEntity
 * @see PhotoEntity
*/
data class AuditItemWithPhotos(
    @Embedded val item: AuditItemEntity,
    @Relation(
        parentColumn = "auditItemId",
        entityColumn = "auditItemId"
    )
    val photos: List<PhotoEntity>
)
