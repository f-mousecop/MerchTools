package com.example.merchtools.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for Section object
@Entity(tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = StoreEntity::class,
            parentColumns = ["storeId"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE   // delete sections when store is deleted
        )
    ]
)
data class SectionEntity(
    @PrimaryKey(autoGenerate = true) val sectionId: Long = 0,
    val storeId: Long,
    val name: String?
)