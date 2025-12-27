package com.example.merchtools.ui.components

import android.content.Context
import android.graphics.Rect
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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

    val latestOnDetected = rememberUpdatedState(onBarcodeDetected)

    var lastBarcode by remember { mutableStateOf<String?>(null) }
    var hasHandledResult by remember { mutableStateOf(false) }
    var boundingRect by remember { mutableStateOf<Rect?>(null) }

    // Ensure camera controller lives across recompositions
    val cameraController = remember(context) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    // We need to create barcode scanner once
    val barcodeScanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_QR_CODE
            )
            .build()

        BarcodeScanning.getClient(options)
    }

    /**
     * We need to set up MLKit Analyzer + bind to lifecycle, and clean up on dispose to
     * prevent memory leaks from our end
     */
    DisposableEffect(lifecycleOwner, cameraController, barcodeScanner) {
        val executor = ContextCompat.getMainExecutor(context)

        Log.d("ScanCode", "${barcodeScanner.hashCode()} Created")

        cameraController.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { result ->
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

        onDispose {
            cameraController.clearImageAnalysisAnalyzer()
            cameraController.unbind()
            barcodeScanner.close()

            Log.d("ScanCode", "${barcodeScanner.hashCode()} Disposed")
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { PreviewView(it) },
        update = { it.controller = cameraController}
    )

    // When we get our first barcode, call the callback once
    LaunchedEffect(lastBarcode) {
        val upc = lastBarcode ?: return@LaunchedEffect
        if (upc.isBlank()) return@LaunchedEffect

        cameraController.clearImageAnalysisAnalyzer()
        cameraController.unbind()

        triggerVibration(context)

        delay(300)
        latestOnDetected.value(upc)
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

