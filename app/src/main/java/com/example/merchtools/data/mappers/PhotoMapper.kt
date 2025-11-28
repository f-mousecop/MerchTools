package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.PhotoEntity
import com.example.merchtools.domain.model.Photo

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        photoId = photoId,
        auditItemId = auditItemId,
        uri = uri,
        createdAt = createdAt
    )
}

fun Photo.toPhotoEntity(): PhotoEntity {
    return PhotoEntity(
        photoId = photoId,
        auditItemId = auditItemId,
        uri = uri,
        createdAt = createdAt
    )
}