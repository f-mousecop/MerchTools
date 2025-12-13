package com.example.merchtools.ui.components

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.delay

@Composable
fun ScanCode(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lastBarcode by remember { mutableStateOf<String?>(null) }
    var hasHandledResult by remember { mutableStateOf(false) }
    var boundingRect by remember { mutableStateOf<Rect?>(null) }

    // Ensure camera controller lives across recompositions
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                // Config ML Kit scanner for UPC_A/CODE_128
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_UPC_E
                    )
                    .build()

                val barcodeScanner = BarcodeScanning.getClient(options)

                // Set up image analysis analyzer for barcode detection
                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    MlKitAnalyzer(
                        listOf(barcodeScanner), // Pass the barcode scanner
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED, // Use view-referenced coordinates
                        ContextCompat.getMainExecutor(ctx) // Use the main executor
                    ) { result: MlKitAnalyzer.Result? ->
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (!barcodeResults.isNullOrEmpty() && !hasHandledResult) {
                            val first = barcodeResults.first()
                            val rawValue = first.rawValue

                            if (!rawValue.isNullOrBlank()) {
                                lastBarcode = rawValue
                                boundingRect = first.boundingBox
                                hasHandledResult = true
                            }
                        }
                    }
                )

                cameraController.bindToLifecycle(lifecycleOwner)
                this.controller = cameraController
            }
        }
    )

    // When we get our first barcode, call the callback once
    LaunchedEffect(lastBarcode) {
        val upc = lastBarcode
        if (upc != null && upc.isNotBlank()) {
            triggerVibration(context)
            delay(1000)
            onBarcodeDetected(upc)
        }
    }

    DrawBarcodeRectOverlay(rect = boundingRect)
}

@Composable
fun DrawBarcodeRectOverlay(rect: Rect?) {
    val composeRect = rect?.toComposeRect()

    composeRect?.let {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(it.left, it.top),
                size = Size(it.width, it.height),
                style = Stroke(width = 5f)
            )
        }
    }
}

@Suppress("DEPRECATION")
fun triggerVibration(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
}

