package com.example.merchtools.data.local.mock

import com.example.merchtools.domain.model.Section
import com.example.merchtools.domain.model.Store

object MockStores {
    val stores = listOf(
        Store(
            name = "Target #123",
            address = "123 Main St",
            sections = listOf(
                Section(
                    storeId = 1,
                    name = "Section 1",
            ),
                Section(
                    storeId = 1,
                    name = "Section 2",
                )
            )
        ),

        Store(
            name = "Walmart #456",
            address = "456 Elm St",
            sections = listOf(
                Section(
                    storeId = 2,
                    name = "Section 1",
                ),
                Section(
                    storeId = 2,
                    name = "Section 2",
                )
            )
        ),

        Store(
            name = "Costco #789",
            address = "789 Oak St",
            sections = listOf(
                Section(
                    storeId = 3,
                    name = "Section 1",
                ),
                Section(
                    storeId = 3,
                    name = "Section 2",
                )
            )
        ),

        Store(
            name = "Amazon #012",
            address = "012 Pine St",
            sections = listOf(
                Section(
                    storeId = 4,
                    name = "Section 1",
                ),
                Section(
                    storeId = 4,
                    name = "Section 2",
                )
            )
        )
    )
}