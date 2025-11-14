package com.example.merchtools.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for SKU object
@Entity(tableName = "skus",
    /*foreignKeys = [
        ForeignKey(
            entity = StoreEntity::class,
            parentColumns = ["storeId"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE   // delete sections when store is deleted
        )
    ]*/
)
data class SkuEntity(
    @PrimaryKey(autoGenerate = true) val skuId: Long = 0,
//    val storeId: Long,
    val upc: String,
    val name: String,
    val casePack: String?,
    val brand: String
)