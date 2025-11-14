package com.example.merchtools.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// entity data class for Store object
@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val storeId: Long = 0,
    val name: String,
    val address: String? = null
)