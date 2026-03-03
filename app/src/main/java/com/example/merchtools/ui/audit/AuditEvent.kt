package com.example.merchtools.ui.audit

import com.example.merchtools.domain.model.AuditItem

sealed class AuditEvent {
    data class EditAuditItem(val auditItemId: Long) : AuditEvent()
    data class AddItemBySearch(val upc: String) : AuditEvent()
    // Event to remove a specific audit item
    data class RemoveItem(val item: AuditItem) : AuditEvent()

    // Event for SKU search query
    // added fromSelection to prevent re-expanding when an item is selected from the list
    data class OnSearchQueryChanged(val query: String, val fromSelection: Boolean = false) : AuditEvent()
    
    // Event for SearchBar expanded state
    data class OnSearchExpandedChanged(val expanded: Boolean) : AuditEvent()

    // Event for when a barcode is successfully scanned
    // carries the raw UPC value to the ViewModel to fetch the SKU
    object BarcodeScanned : AuditEvent()
    // Event to save or submit an audit
    object SaveAudit : AuditEvent()
    // Event for when a user confirms discarding changes
    object DiscardChanges : AuditEvent()
}
