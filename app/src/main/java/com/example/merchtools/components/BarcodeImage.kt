package com.example.merchtools.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.merchtools.domain.util.BarcodeGenerator


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

    val bitmap = remember(upc) {
        barcodeGenerator.generate(
            upc,
            widthPx = 600,
            heightPx = 200
        )
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Barcode for $upc",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}