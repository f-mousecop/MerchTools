package com.example.merchtools.domain.repository

import com.example.merchtools.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun insertPhoto(photo: Photo): Long
    suspend fun updatePhoto(photo: Photo)
    suspend fun deletePhoto(photo: Photo)
    suspend fun getPhotoStream(photoId: Long): Flow<Photo?>
    fun getAllPhotosForAuditStream(auditItemId: Long): Flow<List<Photo>>
}