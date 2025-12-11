package com.example.merchtools.ui.feature_scanner

import android.R
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MlKitBarcodeAnalyzer(
    private val barcodeScanner: BarcodeScanner,
    private val scope: CoroutineScope,
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image ?: run {
            image.close()
            return
        }

        val rotationDegrees = image.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)



        scope.launch {
            try {
                val barcodes = barcodeScanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue
                            val valueType = barcode.valueType
                            when (valueType) {
                                Barcode.FORMAT_UPC_A,
                                Barcode.FORMAT_UPC_E,
                                Barcode.FORMAT_CODE_128 -> true
                                else -> false
                            }
                        }
                    }
            } catch (e: Exception) {

            }
        }
    }



}