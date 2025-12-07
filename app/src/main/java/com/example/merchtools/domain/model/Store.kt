package com.example.merchtools.domain.model

data class Store(
    val storeId: Long = 0L,
    val name: String = "",
    val address: String? = null,
    val sections: List<Section> = emptyList()
)
