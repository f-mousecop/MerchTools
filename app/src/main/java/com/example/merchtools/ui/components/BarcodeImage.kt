package com.example.merchtools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.merchtools.domain.util.BarcodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * A composable that displays a barcode image generated from a given UPC string.
 *
 * This function takes a UPC code and a [BarcodeGenerator] instance to create and display a barcode.
 * It handles the asynchronous generation of the barcode image, showing a loading indicator
 * while the image is being prepared. If the provided UPC is blank, it displays a "No UPC"
 * placeholder text. Once the barcode is generated, it's displayed as an [Image].
 *
 * @param upc The Universal Product Code (UPC) string to be encoded into a barcode.
 *            If this is blank, a placeholder is shown.
 * @param barcodeGenerator An instance of [BarcodeGenerator] used to create the barcode bitmap.
 * @param modifier The [Modifier] to be applied to the composable.
 */
@Composable
fun BarcodeImage(
    upc: String,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {

    if (upc.isBlank()) {
        // Show a simple placeholder instead of crashing
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No UPC",
                style = MaterialTheme.typography.bodySmall
            )
        }
        return
    }

    var imageBitmap by remember(upc) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(upc) {
        withContext(Dispatchers.Default) {
            val bmp = barcodeGenerator.generate(
                upc = upc,
                widthPx = 400,
                heightPx = 120
            )
            imageBitmap = bmp.asImageBitmap()
        }
    }

    if (imageBitmap == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        }
    } else {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = "Barcode for $upc",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}