package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.StoreEntity
import com.example.merchtools.data.local.relations.StoreWithSections
import com.example.merchtools.domain.model.Store

fun StoreEntity.toStore(): Store {
    return Store(
        name = name,
        address = address
    )
}

fun Store.toStoreEntity(): StoreEntity {
    return StoreEntity(
        name = name,
        address = address
    )
}

/**
 * Maps the database-level relation object [StoreWithSections] to
 * the domain-level [Store] model
 *
 * This function takes care of mapping the parent Store and its child Sections
 */
fun StoreWithSections.toDomain(): Store {
    return store.toStore().copy(
        sections = sections.map { it.toSection() }
    )
}