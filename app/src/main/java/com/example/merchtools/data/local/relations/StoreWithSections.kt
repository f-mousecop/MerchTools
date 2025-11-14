package com.example.merchtools.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.merchtools.data.local.entity.SectionEntity
import com.example.merchtools.data.local.entity.StoreEntity

/**
 * This class captures the relationship between an [StoreEntity] and a list of 0 or more attached [SectionEntity]'s
 * used by Room to fetch the related data
 * @see StoreEntity
 * @see SectionEntity
 * */
data class StoreWithSections(
    @Embedded val store: StoreEntity,
    @Relation(
        parentColumn = "storeId",
        entityColumn = "storeId"
    )
    val sections: List<SectionEntity>
)
