package com.example.merchtools.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


fun Bitmap.toBase64Png(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val bytes = outputStream.toByteArray()
    // NO_WRAP so it doesn't insert newlines inside the base64 string
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}