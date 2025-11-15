package com.example.merchtools.domain.repository

import com.example.merchtools.data.local.entity.SectionEntity
import com.example.merchtools.domain.model.Section
import kotlinx.coroutines.flow.Flow

/**
 * Repo that provides insert, update, delete, get, and get all methods
 * for [SectionEntity]
 */
interface SectionRepository {
    /**
     * Retrieve all [SectionEntity]'s from the data source
     * @return A list of [Section]
     */
    fun getAllSectionsStream(storeId: Long): Flow<List<Section>>

    /**
     * Retrieve a [SectionEntity] from the data source
     * @param sectionId The id of the [SectionEntity] to retrieve
     * @return A [Section]
     */
    fun getSectionStream(sectionId: Long): Flow<Section?>

    /**
     * Insert a [SectionEntity] into the data source
     * @param section The [Section] to insert
     */
    suspend fun insertSection(section: Section)

    /**
     * Update a [SectionEntity] in the data source
     * @param section The [Section] to update
     */
    suspend fun updateSection(section: Section)

    /**
     * Delete a [SectionEntity] from the data source
     * @param section The [Section] to delete
     */
    suspend fun deleteSection(section: Section)

}