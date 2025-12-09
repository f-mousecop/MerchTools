package com.example.merchtools.util

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentAdapter.*
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.webkit.WebView
import com.example.merchtools.domain.model.Audit
import java.io.File


/**
 * Generates a PDF report from the content of a WebView and initiates the Android print process.
 *
 * This function takes a WebView instance, which is expected to be loaded with the HTML content
 * of the audit report. It then uses the Android `PrintManager` to create a print job.
 * The user will be presented with the standard Android print preview screen, where they can
 * choose to save the content as a PDF or send it to a physical printer.
 *
 * The generated document is configured for ISO A4 paper size and the default file name is
 * constructed using the audit's store name @see [Audit.store] and creator @see [Audit.createdBy].
 *
 * @param webView The WebView containing the rendered HTML report to be printed. If null, the function does nothing.
 * @param context The application context, required to access the `PrintManager` system service.
 * @param audit The Audit object containing details like store name and creator, used for naming the report.
 */
fun generateAuditPdfReport(
    context: Context,
    webView: WebView,
    audit: Audit,
//    onResult: (Result<File>) -> Unit
) {
    /**
     * COME BACK TO THIS LATER TRYING TO FIGURE OUT HOW TO USE
     * PDFDOCUMENT to print and share using intents
     */
    /*try {
        // 1) Set up PDF attributes (A4, 300 dpi, no margins)
        val jobName = "Audit_Report_${audit.auditId}"

        val printAttrs = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        val printAdapter: PrintDocumentAdapter =
            webView.createPrintDocumentAdapter(jobName)

        val pdfFile = File(context.cacheDir, "audit_${audit.auditId}.pdf")

        try {
            val pfd = ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_CREATE or
                        ParcelFileDescriptor.MODE_TRUNCATE or
                        ParcelFileDescriptor.MODE_WRITE_ONLY
            )

            // 1) Ask WebView to lay out for print
            printAdapter.onLayout(
                null,
                printAttrs,
                CancellationSignal(),
                object : LayoutResultCallback() {
                    override fun onLayoutFinished(
                        info: PrintDocumentInfo?,
                        changed: Boolean
                    ) {
                        // 2) Ask WebView to actually write the PDF into our file
                        printAdapter.onWrite(
                            arrayOf(PageRange.ALL_PAGES),
                            pfd,
                            CancellationSignal(),
                            object : WriteResultCallback() {
                                override fun onWriteFinished(pages: Array<PageRange>) {
                                    super.onWriteFinished(pages)
                                    onResult(Result.success(pdfFile))
                                }

                                override fun onWriteFailed(error: CharSequence?) {
                                    super.onWriteFailed(error)
                                    onResult(
                                        Result.failure(
                                            Exception(error?.toString() ?: "Write failed")
                                        )
                                    )
                                }
                            }
                        )
                    }

                    override fun onLayoutFailed(error: CharSequence?) {
                        super.onLayoutFailed(error)
                        onResult(
                            Result.failure(
                                Exception(error?.toString() ?: "Layout failed")
                            )
                        )
                    }
                },
                null
            )
        } catch (e: Exception) {
            onResult(Result.failure(e))
        }
    }
}*/


    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter =
        webView.createPrintDocumentAdapter(
            "Audit_Report_${audit.store?.name}_${audit.createdBy}"
        )
    val printAttributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .build()
    printManager.print(
        "TestAuditPDF",
        printAdapter,
        printAttributes
    )
}
