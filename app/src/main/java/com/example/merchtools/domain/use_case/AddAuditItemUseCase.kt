package com.example.merchtools.domain.use_case

import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.AuditItemRepository
import javax.inject.Inject


/**
 * Use case responsible for creating and adding a new, empty audit item to an existing audit.
 *
 * This class encapsulates the business logic for creating a default `AuditItem`
 * and persisting it via the `AuditItemRepository`. The new item is initialized
 * with default values (e.g., count of 0, empty SKU) and associated with a
 * specific audit via its ID.
 *
 * @param auditItemRepository The repository for accessing and modifying audit item data.
 */
class AddAuditItemUseCase @Inject constructor(
    private val auditItemRepository: AuditItemRepository
) {
    suspend operator fun invoke(
        auditId: Long,
        sku: Sku? = null
        ): AuditItem {
        val newAuditItem = AuditItem(
            auditId = auditId,
            count = 0,
            note = null,
            sku = sku ?: Sku(
                upc = "",
                name = "",
                casePack = null,
                brand = ""
            )
        )

        auditItemRepository.insertAuditItem(newAuditItem)
        return newAuditItem
    }
}