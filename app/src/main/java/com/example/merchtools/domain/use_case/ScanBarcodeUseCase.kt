package com.example.merchtools.domain.use_case

import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import jakarta.inject.Inject

class ScanBarcodeUseCase @Inject constructor(
    private val skuRepository: SkuRepository
) {
    suspend operator fun invoke(rawValue: String): ScanResult {
        return try {
            val sku = skuRepository.getSkuByUpc(rawValue)
            if (sku != null) {
                ScanResult.Existing(sku, rawValue)
            } else {
                ScanResult.NotFound(rawValue)
            }
        } catch (e: Exception) {
            ScanResult.Error(e)
        }
    }
}


sealed class ScanResult {
    data class Existing(val sku: Sku, val rawValue: String) : ScanResult()
    data class NotFound(val upc: String) : ScanResult()
    data class Error(val throwable: Throwable) : ScanResult()
}