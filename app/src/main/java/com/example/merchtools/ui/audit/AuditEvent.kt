package com.example.merchtools.ui.audit

import com.example.merchtools.domain.model.AuditItem

sealed class AuditEvent {
    // Event for when a property of a specific AUditItem is changed
    data class OnItemChanged(val item: AuditItem, val itemIndex: Int) : AuditEvent()

    // Event for when a user adds a new, blank item to the audit
    data object AddNewItem : AuditEvent()

    data class EditAuditItem(val auditItemId: Long) : AuditEvent()


    // Event for when a barcode is successfully scanned
    // carries the raw UPC value to the ViewModel to fetch the SKU
    data object BarcodeScanned : AuditEvent()

    data class AddItemBySearch(val upc: String) : AuditEvent()

    // Event to remove a specific audit item
    data class RemoveItem(val item: AuditItem) : AuditEvent()

    // Event to add a photo to a specific audit item
    data class AddPhotoToItem(val itemIndex: Int) : AuditEvent()

    // Event to remove a photo from audit item
    data class RemovePhotoFromItem(val photoUri: String, val itemIndex: Int) : AuditEvent()

    // Event to save or submit an audit
    data object SaveAudit : AuditEvent()

    // Event for when a user confirms discarding changes
    data object DiscardChanges : AuditEvent()
}