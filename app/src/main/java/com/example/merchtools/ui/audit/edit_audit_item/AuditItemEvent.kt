package com.example.merchtools.ui.audit.edit_audit_item

sealed class AuditItemEvent {
    data class OnNoteChanged(val userInput: String) : AuditItemEvent()

    // Event for user changing the count
    data class OnCountChanged(val newCount: Int) : AuditItemEvent()

    // Event to add a photo to a specific audit item
    data object AddPhotoToItem : AuditItemEvent()

    // Event to remove a photo from audit item
    data object RemovePhotoFromItem : AuditItemEvent()

    // Event to save changed
    data object SaveAuditItem : AuditItemEvent()
}