package com.example.merchtools.domain.repository

import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Photo
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.data.local.entity.AuditEntity
import com.example.merchtools.data.local.relations.AuditWithItems
import kotlinx.coroutines.flow.Flow

/**
 * Repo that provides insert, update, delete, get, get all, get all with [AuditItem]
 * and [Photo] from a given data source
 */
interface AuditRepository {
    /**
     * Retrieve all [Audit] from the data source
     *
     * */
    fun getAllAuditsStream(auditId: Long): Flow<List<Audit>>

    /**
     * Retrieve a [AuditEntity] from the data source
     * @param auditId the id of the [Audit] to retrieve
     * @return [Audit]
     * */
    fun getAuditStream(auditId: Long): Flow<Audit?>

    /**
     * Delete an [Audit] from the data source
     * @param audit the [Audit] to delete
     */
    suspend fun deleteAudit(audit: Audit)

    /**
     * Insert an [Audit] in the data source
     * @param audit the [Audit] to insert
     */
    suspend fun insertAudit(audit: Audit)

    /**
     * Update an [Audit] in the data source
     * @param audit the [Audit] to update
     */
    suspend fun updateAudit(audit: Audit)

    /**
     * Fetches current/in-progress [Audit] with [AuditItem]'s and
     * [Photo] if applicable
     * @return [AuditWithItems]
     */
    suspend fun getCurrentAuditWithItems(): Audit?

    /**
     * Fetches a list of [Audit] containing [AuditItem]'s and associated
     * [Photo]'s if applicable for History screen
     * @return [List<[Audit]>]
     */
    fun getAuditHistory(): Flow<List<Audit>>
}