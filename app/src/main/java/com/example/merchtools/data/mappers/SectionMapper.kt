package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.SectionEntity
import com.example.merchtools.domain.model.Section

fun SectionEntity.toSection(): Section {
    return Section(
        storeId = storeId,
        name = name
    )
}

fun Section.toSectionEntity(): SectionEntity {
    return SectionEntity(
        storeId = storeId,
        name = name
    )
}