package com.example.merchtools.data.local.mock

import com.example.merchtools.domain.model.Sku

object MockSkus {
    val skus = listOf(
        Sku(
            upc = "123456789012",
            name = "Product 1",
            casePack = "16.9oz 6pk",
            brand = "Pepsi"
        ),

        Sku(
            upc = "987654321098",
            name = "Product 2",
            casePack = "16.9oz 12pk",
            brand = "Pepsi"
        ),

        Sku(
            upc = "555555555555",
            name = "Product 3",
            casePack = "7.5oz 10pk",
            brand = "Mountain Dew"
        ),

        Sku(
            upc = "111111111111",
            name = "Product 4",
            casePack = "12oz 12pk",
            brand = "Mountain Dew"
        ),

        Sku(
            upc = "222222222222",
            name = "Product 5",
            casePack = "2L",
            brand = "Code Red"
        ),

        Sku(
            upc = "333333333333",
            name = "Product 6",
            casePack = "20oz 24pk",
            brand = "Baja Blast"
        )
    )
}