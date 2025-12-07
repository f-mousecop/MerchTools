package com.example.merchtools.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.util.BarcodeGenerator

/**
 * Need to finish the implementation for generating PDF via PdfDocument
 */
fun Context.generateAuditPdfReport(
    audit: Audit,
    barcodeGenerator: BarcodeGenerator,
    onResult: (Result<android.net.Uri>) -> Unit
) {
    val pdfDocument = PdfDocument()
}
