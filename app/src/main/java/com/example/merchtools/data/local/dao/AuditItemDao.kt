package com.example.merchtools.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.merchtools.data.local.entity.AuditItemEntity
import com.example.merchtools.data.local.relations.AuditItemWithPhotos
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(auditItem: AuditItemEntity): Long

    @Update
    suspend fun update(auditItem: AuditItemEntity)

    @Delete
    suspend fun delete(auditItem: AuditItemEntity)

    @Query("SELECT * FROM audit_items WHERE auditItemId = :id")
    fun getAuditItem(id: Long): Flow<AuditItemEntity?>

    @Query("SELECT * FROM audit_items WHERE auditId = :auditId")
    fun getAllAuditItems(auditId: Long): Flow<List<AuditItemEntity>>

    @Transaction
    @Query("SELECT * FROM audit_items WHERE auditId = :auditId")
    suspend fun getAuditItemsWithPhotos(auditId: Long): List<AuditItemWithPhotos>
}