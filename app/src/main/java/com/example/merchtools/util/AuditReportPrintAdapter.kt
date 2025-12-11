package com.example.merchtools.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import androidx.core.content.FileProvider
import com.example.merchtools.domain.model.Report
import com.example.merchtools.domain.util.BarcodeGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.ceil

class AuditReportPrintAdapter(
    private val context: Context,
    private val report: Report,
    private val barcodeGenerator: BarcodeGenerator,
    private val onResult: (Result<Uri>) -> Unit
) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null
    private var pageCount: Int = 0

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?,
    ) {
        pdfDocument?.close()
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val pages = computePageCount(newAttributes)
        this.pageCount = pages

        if (pages > 0) {
            PrintDocumentInfo.Builder("Audit_Report_${report.storeName}_${report.createdBy}")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
                .also { info ->
                    // Content layout reflow is complete
                    callback.onLayoutFinished(info, true)
                }
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed")
        }

    }

    private fun computePageCount(printAttributes: PrintAttributes): Int {
        val itemsPerPage =
            if (printAttributes.mediaSize?.isPortrait != false) 2 else 4

        if (itemsPerPage <= 0) return 1

        // Determine number of print items
        return ceil(report.items.size / itemsPerPage.toDouble())
            .toInt()
            .coerceAtLeast(1)
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback,
    ) {
        val doc = pdfDocument ?: run {
            val error = "Document not initialized"
            onResult(Result.failure(IOException(error)))
            callback.onWriteFailed(error)
            return
        }

        val file = File(context.cacheDir, "audit_${report.storeName}_${report.createdBy}.pdf")

        try {
            for (pageIndex in 0 until pageCount) {
                if (!pageInRange(pageRanges, pageIndex)) continue
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    doc.close()
                    pdfDocument = null
                    return
                }

                val page = doc.startPage(pageIndex)

                drawPage(page, pageIndex)
                doc.finishPage(page)
                println("DEBUG: Page count $pageCount")
            }

            FileOutputStream(file).use { fileOut ->
                FileOutputStream(destination.fileDescriptor).use { destOut ->
                    val combinedOut = TeeOutputStream(fileOut, destOut)
                    doc.writeTo(combinedOut)
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            onResult(Result.success(uri))

        } catch (e: IOException) {
            onResult(Result.failure(e))
            callback.onWriteFailed(e.toString())
            return
        } finally {
            doc.close()
            pdfDocument = null
        }
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    private fun pageInRange(pageRanges: Array<out PageRange>, pageIndex: Int): Boolean {
        for (range in pageRanges) {
            if (pageIndex >= range.start && pageIndex <= range.end) return true
        }
        return false
    }


    private fun drawPage(page: PdfDocument.Page, pageNumber: Int) {
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val titleBaseLine = 72f
        val leftMargin = 54f

        // Title
        paint.color = Color.BLACK
        paint.textSize = 24f
        canvas.drawText(
            "Audit Report - Store: ${report.storeName}",
            leftMargin,
            titleBaseLine,
            paint
        )

        paint.textSize = 12f
        val metaLine = titleBaseLine + 20f
        canvas.drawText(
            "Created by: ${report.createdBy}",
            leftMargin,
            metaLine,
            paint
        )
        canvas.drawText(
            "Completed: ${report.completedAt}",
            leftMargin,
            metaLine + 14f,
            paint
        )

        // Simple example of paginated items
        val itemsPerPage = 2 // keep in sync with computePageCount()
        val startIndex = pageNumber * itemsPerPage

        var y = metaLine + 60f

        for (i in 0 until itemsPerPage) {
            val itemIndex = startIndex + i
            if (itemIndex >= report.items.size) break

            val row = report.items[itemIndex]
            val barcode = barcodeGenerator.generate(
                row.upc,
                widthPx = 400,
                heightPx = 120
            )
            canvas.drawBitmap(barcode,
                leftMargin,
                y,
                paint
            )
            y += 160f

            canvas.drawLine(
                leftMargin,
                y,
                leftMargin + 400f,
                y,
                paint
            )
            y += 24f

            paint.textSize = 16f
            paint.color = Color.BLACK
            canvas.drawText("Count: ${row.count}",
                leftMargin,
                y,
                paint)
            y += 18f

            paint.textSize = 12f
            canvas.drawText("UPC: ${row.upc}",
                leftMargin,
                y,
                paint
            )
            y += 14f
            canvas.drawText("SKU: ${row.skuName}",
                leftMargin,
                y,
                paint
            )
            y += 14f
            canvas.drawText(row.brandInfo,
                leftMargin,
                y,
                paint
            )
            y += 14f

            canvas.drawText("Note: ${row.note}",
                leftMargin,
                y,
                paint)
            y += 24f
            canvas.drawLine(
                leftMargin,
                y,
                leftMargin + 400f,
                y,
                paint
            )
            y += 24f
        }

        // Little debug box so you can see something obvious
        /*paint.color = Color.BLUE
        canvas.drawRect(100f, 100f, 172f, 172f, paint)*/
    }
}

/**
 * Helper OutputStream that forwards all write ops to two other streams
 */
private class TeeOutputStream(private val out1: OutputStream, private val out2: OutputStream) : OutputStream() {
    override fun write(b: Int) {
        out1.write(b)
        out2.write(b)
    }

    override fun write(b: ByteArray?) {
        out1.write(b)
        out2.write(b)
    }

    override fun write(b: ByteArray?, off: Int, len: Int) {
        out1.write(b, off, len)
        out2.write(b, off, len)
    }

    override fun flush() {
        out1.flush()
        out2.flush()
    }

    override fun close() {
        try {
            out1.close()
        } finally {
            out2.close()
        }
    }

}