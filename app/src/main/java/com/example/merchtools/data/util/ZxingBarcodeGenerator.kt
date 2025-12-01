package com.example.merchtools.data.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.merchtools.domain.util.BarcodeGenerator
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import javax.inject.Inject

/**
 * An implementation of the [BarcodeGenerator] interface that uses the ZXing (Zebra Crossing)
 * library to create barcode images.
 *
 * This class is responsible for encoding a given string, typically a UPC (Universal Product Code),
 * into a [Bitmap] representation of a barcode. It uses the `CODE_128` format for encoding.
 *
 * @see BarcodeGenerator
 * @see com.google.zxing.MultiFormatWriter
 */
class ZxingBarcodeGenerator @Inject constructor(): BarcodeGenerator {
    override fun generate(
        upc: String,
        widthPx: Int,
        heightPx: Int,
    ): Bitmap {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            upc,
            BarcodeFormat.CODE_128,
            widthPx,
            heightPx
        )

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        for (x in 0 until widthPx) {
            for (y in 0 until heightPx) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }
        return bitmap
    }
}