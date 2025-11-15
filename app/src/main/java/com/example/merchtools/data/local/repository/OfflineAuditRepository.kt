package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.AuditDao
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.data.mappers.toAudit
import com.example.merchtools.data.mappers.toAuditEntity
import com.example.merchtools.data.mappers.toDomain
import com.example.merchtools.domain.model.Audit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineAuditRepository @Inject constructor(val auditDao: AuditDao) : AuditRepository {
    override fun getAllAuditsStream(auditId: Long): Flow<List<Audit>> {
        return auditDao.getAllAudits(auditId).map { entityList ->
            entityList.map { it.toDomain() }
        }
    }

    override fun getAuditStream(auditId: Long): Flow<Audit?> {
        return auditDao.getAudit(auditId).map { entity ->
            entity?.toAudit()
        }
    }

    override suspend fun deleteAudit(audit: Audit) {
        auditDao.delete(audit.toAuditEntity())
    }

    override suspend fun insertAudit(audit: Audit) {
        // We must map the domain model back to an entity before giving it
        // to the DAO
        auditDao.insert(audit.toAuditEntity())
    }

    override suspend fun updateAudit(audit: Audit) {
        auditDao.update(audit.toAuditEntity())
    }

    override suspend fun getCurrentAuditWithItems(): Audit? {
        // Get the data-layer object from the DAO
        val auditWithItems = auditDao.getCurrentAuditWithItems()
        // Map to the domain model before returning
        return auditWithItems?.toDomain()
    }

    override fun getAuditHistory(): Flow<List<Audit>> {
        return auditDao.getAuditHistory().map { entityList ->
            entityList.map { it.toAudit() }
        }
    }
}