package com.example.merchtools.data.local.repository

import com.example.merchtools.domain.model.AuditItem
import kotlinx.coroutines.flow.Flow

interface AuditItemRepository {
    fun getAuditItemStream(auditId: Long): Flow<AuditItem?>
    fun getAllAuditItemsStream(auditId: Long): Flow<List<AuditItem>>
    suspend fun insertAuditItem(auditItem: AuditItem): Long
    suspend fun updateAuditItem(auditItem: AuditItem)
    suspend fun deleteAuditItem(auditItem: AuditItem)
}