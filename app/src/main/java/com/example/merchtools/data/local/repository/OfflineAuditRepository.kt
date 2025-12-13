package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.AuditDao
import com.example.merchtools.data.mappers.toAudit
import com.example.merchtools.data.mappers.toAuditEntity
import com.example.merchtools.data.mappers.toDomain
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.repository.AuditRepository
import com.example.merchtools.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineAuditRepository @Inject constructor(
    val auditDao: AuditDao,
    private val clock: Clock
) : AuditRepository {
    override fun getAllAuditsStream(): Flow<Resource<List<Audit>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                auditDao.getAllAudits().map { entityList ->
                    entityList.map { it.toDomain() }
                }.collect { auditList ->
                    emit(Resource.Success(auditList))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override fun getAuditStream(auditId: Long): Flow<Audit?> {
        return auditDao.getAudit(auditId)
            .map { it?.toDomain() }
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

    override suspend fun getCurrentAuditId(): Long? {
        return getCurrentAuditWithItems()?.auditId
    }

    override suspend fun startNewAudit(
        storeId: Long,
        createdBy: String,
    ): Long {
        val now = Instant.now(clock)
        val newAudit = Audit(
            storeId = storeId,
            startedAt = now,
            createdBy = createdBy,
        )
        return auditDao.insert(newAudit.toAuditEntity())
    }

    override fun getAuditHistory(): Flow<Resource<List<Audit>>> {
        return flow {
            emit(Resource.Loading(true))

            try {
                auditDao.getAuditHistory().map { entityList ->
                    entityList.map { it.toAudit() }
                }.collect { auditList ->
                    emit(Resource.Success(auditList))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            } finally {
                emit(Resource.Loading(false))
        }
        }
    }
}