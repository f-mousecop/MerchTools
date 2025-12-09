package com.example.merchtools.domain.use_case

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import com.example.merchtools.domain.model.Audit
import jakarta.inject.Inject
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil

/**
 * Need to modify this use case so that it invokes an AuditReportPrintAdapter
 * @return [PrintDocumentAdapter]
 */
class GenerateReportUseCase @Inject constructor(
    private val context: Context,
    private val audit: Audit
) : PrintDocumentAdapter() {

    private var printItemCount: Int = audit.items.size
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

        if (pages > 0) {
            PrintDocumentInfo.Builder("Audit_Report_${audit.store?.name}_${audit.createdBy}")
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
        var itemsPerPage =
            if (printAttributes.mediaSize?.isPortrait != false) 4 else 6

        if (itemsPerPage <= 0) return 1

        // Determine number of print items
        return ceil(audit.items.size / itemsPerPage.toDouble())
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
            callback.onWriteFailed("Document not initialized")
            return
        }

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
            }

            doc.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
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
            "Audit Report - Store: ${audit.store?.name ?: "-"}",
            leftMargin,
            titleBaseLine,
            paint
        )

        paint.textSize = 12f
        val metaLine = titleBaseLine + 20f
        canvas.drawText(
            "Created by: ${audit.createdBy ?: "—"}",
            leftMargin,
            metaLine,
            paint
        )
        canvas.drawText(
            "Completed: ${audit.completedAt?.toString() ?: "—"}",
            leftMargin,
            metaLine + 14f,
            paint
        )

        // Simple example of paginated items
        val itemsPerPage = 4 // keep in sync with computePageCount()
        val startIndex = pageNumber * itemsPerPage

        var y = metaLine + 40f

        for (i in 0 until itemsPerPage) {
            val itemIndex = startIndex + i
            if (itemIndex >= audit.items.size) break

            val item = audit.items[itemIndex]
            val sku = item.sku

            val line = "${itemIndex + 1}. UPC: ${sku?.upc ?: "—"}  " +
                    "Name: ${sku?.name ?: "—"}  " +
                    "Qty: ${item.count}"

            canvas.drawText(line, leftMargin, y, paint)
            y += 18f
        }

        // Little debug box so you can see something obvious
        paint.color = Color.BLUE
        canvas.drawRect(100f, 100f, 172f, 172f, paint)
    }
}
