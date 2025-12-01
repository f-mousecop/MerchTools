package com.example.merchtools.domain.util

import android.graphics.Bitmap

interface BarcodeGenerator {
    fun generate(upc: String, widthPx: Int, heightPx: Int): Bitmap
}