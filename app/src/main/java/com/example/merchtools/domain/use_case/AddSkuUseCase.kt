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
            upc = "",
            name = "",
            casePack = null,
            brand = ""
        )

        skuRepository.insert(newSku)
        return newSku
    }
}