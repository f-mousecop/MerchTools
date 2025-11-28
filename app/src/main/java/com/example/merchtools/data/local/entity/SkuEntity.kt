package com.example.merchtools.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// entity data class for SKU object
@Entity(tableName = "skus")
data class SkuEntity(
    @PrimaryKey(autoGenerate = true) val skuId: Long = 0,
    val upc: String,
    val name: String,
    val casePack: String?,
    val brand: String
)