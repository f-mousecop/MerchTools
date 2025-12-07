package com.example.merchtools.ui.store

import com.example.merchtools.domain.model.Store

sealed class StoreCatalogEvent {
    data object ShowAddStoreDialog : StoreCatalogEvent()
    data object HideAddStoreDialog : StoreCatalogEvent()
    data class OnStoreNameChanged(val name: String) : StoreCatalogEvent()
    data object AddNewStore : StoreCatalogEvent()
    data class RemoveStore(val store: Store) : StoreCatalogEvent()
    data class EditStore(val storeId: Long) : StoreCatalogEvent()
}