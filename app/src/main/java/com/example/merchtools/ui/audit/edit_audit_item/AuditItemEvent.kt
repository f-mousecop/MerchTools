package com.example.merchtools.ui.audit.edit_audit_item

import com.example.merchtools.domain.model.AuditItem

sealed class AuditItemEvent {
    // Event for when a property of a specific AUditItem is changed
    data class OnItemChanged(val item: AuditItem) : AuditItemEvent()

    // Event for user input into Audit Item fields
    data class OnItemFieldChanged(val userInput: String) : AuditItemEvent()

    data class OnNameChanged(val userInput: String) : AuditItemEvent()
    data class OnCasePackChanged(val userInput: String) : AuditItemEvent()
    data class OnBrandChanged(val userInput: String) : AuditItemEvent()
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