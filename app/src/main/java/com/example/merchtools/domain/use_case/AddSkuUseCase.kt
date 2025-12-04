package com.example.merchtools.domain.use_case

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import javax.inject.Inject


/**
 * A use case for adding a new, empty SKU (Stock Keeping Unit) to the repository.
 *
 * This class encapsulates the business logic for creating and saving a new, default SKU.
 * It interacts with the [SkuRepository] to persist the new SKU data. The primary
 * purpose of this use case is to create a placeholder SKU that can then be edited
 * by the user.
 *
 * @param skuRepository The repository for handling SKU data operations.
 */
class AddSkuUseCase @Inject constructor(
    private val skuRepository: SkuRepository
){
    suspend operator fun invoke(): Sku {
        val newSku = Sku(
            skuId = 0,
            upc = "",
            name = "",
            casePack = null,
            brand = ""
        )

        // Make sure to return the new sku id after inserting it
        // for navigation to edit sku screen
        val id = skuRepository.insert(newSku)
        return newSku.copy(skuId = id)
    }
}