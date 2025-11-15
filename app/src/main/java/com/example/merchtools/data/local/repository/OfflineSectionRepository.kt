package com.example.merchtools.data.local.repository

import com.example.merchtools.data.local.dao.SectionDao
import com.example.merchtools.domain.repository.SectionRepository
import com.example.merchtools.data.mappers.toSection
import com.example.merchtools.data.mappers.toSectionEntity
import com.example.merchtools.domain.model.Section
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineSectionRepository @Inject constructor(val sectionDao: SectionDao) : SectionRepository {
    override fun getAllSectionsStream(storeId: Long): Flow<List<Section>> {
        return sectionDao.getAllSections(storeId).map { entityList ->
            // For each list emitted by the flow, map every entity in that list to a domain model
            entityList.map { it.toSection() }
        }
    }

    override fun getSectionStream(sectionId: Long): Flow<Section?> {
        return sectionDao.getSection(sectionId).map { entity ->
            // If the entity is not null, map it. Otherwise keep it null
            entity?.toSection()
        }
    }

    override suspend fun insertSection(section: Section) {
        // We must map the domain model back to an entity before giving it back to
        // the DAO
        sectionDao.insert(section.toSectionEntity())
    }

    override suspend fun updateSection(section: Section) {
        sectionDao.update(section.toSectionEntity())
    }

    override suspend fun deleteSection(section: Section) {
        sectionDao.delete(section.toSectionEntity())
    }
}