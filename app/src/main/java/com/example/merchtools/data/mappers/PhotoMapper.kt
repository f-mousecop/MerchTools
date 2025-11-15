package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.PhotoEntity
import com.example.merchtools.domain.model.Photo

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        auditItemId = auditItemId,
        uri = uri,
        createdAt = createdAt
    )
}

fun Photo.toPhotoEntity(): PhotoEntity {
    return PhotoEntity(
        auditItemId = auditItemId,
        uri = uri,
        createdAt = createdAt
    )
}