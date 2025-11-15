package com.example.merchtools.data.local

import com.example.merchtools.data.local.dao.AuditItemDao
import com.example.merchtools.data.local.repository.AuditItemRepository
import com.example.merchtools.data.mappers.toAuditItemEntity
import com.example.merchtools.data.mappers.toDomain
import com.example.merchtools.domain.model.AuditItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineAuditItemRepository(val auditItemDao: AuditItemDao) : AuditItemRepository {
    override fun getAuditItemStream(auditId: Long): Flow<AuditItem?> {
        return auditItemDao.getAuditItem(auditId).map { it?.toDomain() }
    }

    override fun getAllAuditItemsStream(auditId: Long): Flow<List<AuditItem>> {
        return auditItemDao.getAllAuditItems(auditId).map { entityList ->
            entityList.map { it.toDomain() }
        }
    }

    override suspend fun insertAuditItem(auditItem: AuditItem): Long {
        return auditItemDao.insert(auditItem.toAuditItemEntity())
    }

    override suspend fun updateAuditItem(auditItem: AuditItem) {
        return auditItemDao.update(auditItem.toAuditItemEntity())
    }

    override suspend fun deleteAuditItem(auditItem: AuditItem) {
        return auditItemDao.delete(auditItem.toAuditItemEntity())
    }
}