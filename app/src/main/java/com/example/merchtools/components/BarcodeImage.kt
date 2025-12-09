package com.example.merchtools.components

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.merchtools.domain.util.BarcodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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