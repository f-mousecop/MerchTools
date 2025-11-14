package com.example.merchtools.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.merchtools.data.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(section: SectionEntity): Long

    @Update
    suspend fun update(section: SectionEntity)

    @Delete
    suspend fun delete(section: SectionEntity)

    @Query("SELECT * FROM sections WHERE sectionId = :id")
    fun getSection(id: Long): Flow<SectionEntity?>

    @Query("SELECT * FROM sections WHERE storeId = :storeId ORDER BY name")
    fun getAllSections(storeId: Long): Flow<List<SectionEntity>>
}