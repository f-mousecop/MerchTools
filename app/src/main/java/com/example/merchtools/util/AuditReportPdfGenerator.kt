package com.example.merchtools.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.merchtools.domain.model.Report
import com.example.merchtools.domain.util.BarcodeGenerator
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

/**
 * Generates a PDF document for an audit report.
 *
 * This class takes a `Report` object and creates a multi-page PDF document
 * summarizing the audit. The generated PDF includes a title, metadata (store name, creator, date),
 * and a paginated list of audited items. Each item is displayed with its barcode, count, UPC,
 * SKU, brand information, and any associated notes.
 *
 * The generated PDF is saved to the application's cache directory and a content `Uri`
 * is returned, which can be used for sharing or viewing the file.
 *
 * @property context The application context, used for accessing resources and the file system.
 * @property report The `Report` data object containing the information to be included in the PDF.
 * @property barcodeGenerator A utility for generating barcode images from UPC strings.
 */
class AuditReportPdfGenerator(
    private val context: Context,
    private val report: Report,
    private val barcodeGenerator: BarcodeGenerator
) {
    /**
     * Generates a PDF document for the audit report.
     *
     * This function orchestrates the creation of a multi-page PDF. It sets up the document
     * attributes (like A4 size), calculates the number of pages required, and then iterates
     * through each page, drawing its content. The content includes a header with store
     * and creator information, followed by a paginated list of audited items, each with
     * its UPC barcode, count, and other details.
     *
     * The final PDF is saved to the application's cache directory and a content URI is
     * generated for it using a `FileProvider`. This URI can be used to share the file or
     * open it with an external PDF viewer.
     *
     * @return A [Result] object containing either:
     *         - `Result.success(Uri)`: The content URI for the generated PDF file.
     *         - `Result.failure(Exception)`: An exception if the PDF generation failed.
     */
    fun generateReport() : Result<Uri> {
        try {
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("id", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()

            val pdfDocument = PrintedPdfDocument(context, printAttributes)
            val pageCount = computePageCount(printAttributes)

            for (pageIndex in 0 until pageCount) {
                val pageInfo = PdfDocument.PageInfo.Builder(
                    pdfDocument.pageWidth,
                    pdfDocument.pageHeight,
                    pageIndex
                ).create()

                val page = pdfDocument.startPage(pageInfo)

                drawPage(page, pageIndex)

                pdfDocument.finishPage(page)
            }

            val file = File(
                context.cacheDir,
                "audit_${report.storeName}_${report.createdBy}.pdf"
            )

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }

            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            return Result.success(uri)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    /**
     * Calculates the total number of pages required for the PDF document.
     *
     * The number of pages is determined by the total number of items in the report
     * and the number of items that can fit on a single page. The layout (portrait or landscape)
     * affects how many items can be displayed per page. A portrait layout fits 2 items,
     * while a landscape layout fits 4. The function ensures at least one page is
     * always returned, even if there are no items.
     *
     * @param printAttributes The print attributes of the document, used to determine
     *                        the page orientation (portrait vs. landscape).
     * @return The total number of pages needed for the report.
     */
    private fun computePageCount(printAttributes: PrintAttributes): Int {
        val itemsPerPage =
            if (printAttributes.mediaSize?.isPortrait != false) 2 else 4

        if (itemsPerPage <= 0) return 1

        // Determine number of print items
        return ceil(report.items.size / itemsPerPage.toDouble())
            .toInt()
            .coerceAtLeast(1)
    }


    /**
     * Draws the content for a single page of the PDF.
     *
     * This function is responsible for rendering the header and the items for a specific page.
     * It draws the report title, store name, creator, and completion date at the top.
     * It then calculates which items from the report list belong on the current page and draws
     * each one, including its barcode, count, UPC, SKU, brand, and any notes.
     *
     * The layout is hardcoded with specific margins, font sizes, and vertical spacing to
     * structure the content on the page.
     *
     * @param page The `PdfDocument.Page` object onto which the content will be drawn.
     * @param pageNumber The zero-based index of the page being drawn. This is used to
     *                   determine which subset of items to render.
     */
    private fun drawPage(page: PdfDocument.Page, pageNumber: Int) {
        val canvas = page.canvas
        val pageWidth = page.info.pageWidth

        /**
         * I have found out that having to use Canvas to draw Pdfdocuments for sharing
         * and printing is annoying...
         */

        val paint = Paint().apply {
            isAntiAlias = true
        }



        // Title
        val titlePaint = TextPaint().apply {
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }

        // Header line
        val headerPaint = Paint().apply {
            strokeWidth = 2f
        }

        // Brand info
        val productPaint = TextPaint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            color = Color.DKGRAY
            textAlign = Paint.Align.CENTER
        }

        // Other items in each row
        val itemsPaint = TextPaint().apply {
            textSize = 12f
            color = Color.DKGRAY
        }

        val centerOffset = (pageWidth / 2).toFloat()

        val itemCountPaint = TextPaint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.DKGRAY
            textAlign = Paint.Align.CENTER
        }


        val titleBaseLine = 72f
        val leftMargin = 54f
        val rightMargin = pageWidth - leftMargin
        val metaLine = titleBaseLine + 20f

        canvas.drawText(
            "Audit Report - Store: ${report.storeName}",
            leftMargin,
            titleBaseLine,
            titlePaint
        )


        canvas.drawText(
            "Created by: ${report.createdBy}",
            leftMargin,
            metaLine,
            itemsPaint
        )
        canvas.drawText(
            "Started: ${report.startedAt}",
            leftMargin,
            metaLine + 14f,
            itemsPaint
        )
        canvas.drawText(
            "Completed: ${report.completedAt}",
            leftMargin,
            metaLine + 28f,
            itemsPaint
        )
        canvas.drawLine(
            leftMargin,
            metaLine + 40f,
            rightMargin,
            metaLine + 40f,
            headerPaint
        )

        // Simple example of paginated items
        val itemsPerPage = 2 // keep in sync with computePageCount()
        val startIndex = pageNumber * itemsPerPage

        var currentY = metaLine + 64f

        for (i in 0 until itemsPerPage) {
            val itemIndex = startIndex + i
            if (itemIndex >= report.items.size) break

            val row = report.items[itemIndex]

            val barcode = barcodeGenerator.generate(
                row.upc,
                widthPx = 400,
                heightPx = 120
            )

            canvas.drawLine(
                leftMargin,
                currentY,
                rightMargin,
                currentY,
                paint
            )

            canvas.drawBitmap(barcode,
                centerOffset - 200f,
                currentY + 40f,
                paint
            )
            currentY += barcode.height + 84f


            canvas.drawText("Count: ${row.count}",
                centerOffset,
                currentY,
                itemCountPaint)

            currentY += 18f

            canvas.drawText(row.brandInfo,
                centerOffset,
                currentY,
                productPaint
            )
            currentY += 14f

            canvas.drawText("UPC: ${row.upc}",
                leftMargin,
                currentY,
                itemsPaint
            )
            currentY += 14f
            canvas.drawText("SKU: ${row.skuName}",
                leftMargin,
                currentY,
                itemsPaint
            )

            // We use StaticLayout to ensure the text wraps at the right margin
            val description = "Note: ${row.note}"
            val textWidth = pageWidth - 108     // pageWidth - leftMargin - rightMargin
            val descriptionLayout = StaticLayout.Builder.obtain(
                description, 0, description.length, itemsPaint, textWidth
            ).build()

            canvas.save()
            canvas.translate(leftMargin, currentY)
            descriptionLayout.draw(canvas)
            canvas.restore()

            currentY += descriptionLayout.height + 24f
        }
    }
}