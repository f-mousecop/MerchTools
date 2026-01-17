package com.example.merchtools.ui.store

import com.example.merchtools.domain.model.Store

sealed class StoreCatalogEvent {
    object ShowAddStoreDialog : StoreCatalogEvent()
    object HideAddStoreDialog : StoreCatalogEvent()
    object AddNewStore : StoreCatalogEvent()

    data class OnStoreNameChanged(val name: String) : StoreCatalogEvent()
    data class RemoveStore(val store: Store) : StoreCatalogEvent()
}