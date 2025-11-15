package com.example.merchtools.data.mappers

import com.example.merchtools.data.local.entity.SkuEntity
import com.example.merchtools.domain.model.Sku

fun SkuEntity.toSku(): Sku {
    return Sku(
        upc = upc,
        name = name,
        casePack = casePack,
        brand = brand
    )
}

fun Sku.toSkuEntity(): SkuEntity {
    return SkuEntity(
        upc = upc,
        name = name,
        casePack = casePack,
        brand = brand
    )
}