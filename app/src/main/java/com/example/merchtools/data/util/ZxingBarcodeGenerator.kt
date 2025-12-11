package com.example.merchtools.data.util

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import com.example.merchtools.domain.util.BarcodeGenerator
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import javax.inject.Inject
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

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

        /**
         * We want to try and encode the [upc] as format UPC_A first
         * as this is the most widely used type of barcode in most retail stores
         * @see BarcodeFormat.UPC_A
         */
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                upc,
                BarcodeFormat.UPC_A,
                widthPx,
                heightPx
            )
        } catch (e: IllegalArgumentException) {
            /**
             * If encoding as UPC_A fails, we fall back to CODE_128
             * This barcode is widely used everywhere
             * @see BarcodeFormat.CODE_128
             */
            MultiFormatWriter().encode(
                upc,
                BarcodeFormat.CODE_128,
                widthPx,
                heightPx
            )

        }

        val bitmap = createBitmap(widthPx, heightPx)
        for (x in 0 until widthPx) {
            for (y in 0 until heightPx) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }
}