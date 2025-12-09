package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.domain.model.Sku

fun SkuEntity.toSku(): Sku {
    return Sku(
        skuId = skuId,
        upc = upc,
        name = name,
        casePack = casePack,
        brand = brand,
        imageUri = imageUri
    )
}

fun Sku.toSkuEntity(): SkuEntity {
    return SkuEntity(
        skuId = skuId,
        upc = upc,
        name = name,
        casePack = casePack,
        brand = brand,
        imageUri = imageUri
    )
}