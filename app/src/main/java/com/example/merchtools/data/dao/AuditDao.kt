package com.example.merchtools.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.merchtools.data.entity.AuditEntity
import com.example.merchtools.data.relations.AuditWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audit: AuditEntity): Long

    @Update
    suspend fun update(audit: AuditEntity)

    @Delete
    suspend fun delete(audit: AuditEntity)

    @Query("SELECT * FROM audits WHERE auditId = :id")
    fun getAudit(id: Long): Flow<AuditEntity?>

    // in-progress audit
    @Transaction
    @Query("SELECT * FROM audits WHERE completedAt IS NULL LIMIT 1")
    suspend fun getCurrentAuditWithItems(): AuditWithItems? // fetch Audit with Audit Items and Photos

    @Transaction
    @Query("SELECT * FROM audits WHERE auditId = :id") // fetch Audit with Audit Items and Photos
    fun getAllAudits(id: Long): Flow<List<AuditWithItems>>

    @Transaction
    @Query("SELECT * FROM audits ORDER BY startedAt DESC")
    fun getAuditHistory(): Flow<List<AuditEntity>>
}