package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.PhotoDao
import com.example.merchtools.domain.repository.PhotoRepository
import com.example.merchtools.data.mappers.toPhoto
import com.example.merchtools.data.mappers.toPhotoEntity
import com.example.merchtools.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflinePhotoRepository @Inject constructor(val photoDao: PhotoDao) : PhotoRepository {
    override suspend fun insertPhoto(photo: Photo): Long {
        return photoDao.insert(photo.toPhotoEntity())
    }

    override suspend fun updatePhoto(photo: Photo) {
        return photoDao.update(photo.toPhotoEntity())
    }

    override suspend fun deletePhoto(photo: Photo) {
        return photoDao.delete(photo.toPhotoEntity())
    }

    override suspend fun getPhotoStream(photoId: Long): Flow<Photo?> {
        return photoDao.getPhoto(photoId).map { it?.toPhoto() }
    }

    override fun getAllPhotosForAuditStream(auditItemId: Long): Flow<List<Photo>> {
        return photoDao.getAllPhotos(auditItemId).map { entityList ->
            entityList.map { it.toPhoto() }
        }
    }
}