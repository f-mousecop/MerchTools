package com.example.merchtools.domain.model

data class Sku(
    val skuId: Long = 0L,
    val upc: String,
    val name: String,
    val casePack: String?,
    val brand: String
)
