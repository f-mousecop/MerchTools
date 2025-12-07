package com.example.merchtools.domain.use_case

import com.example.merchtools.data.local.relations.AuditWithItems
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.util.toBase64Png
import com.example.merchtools.util.toDisplayString
import javax.inject.Inject

class AuditReportHtmlBuilder @Inject constructor(){
    fun buildReportHtml(
        audit: Audit,
        barcodeGenerator: BarcodeGenerator
    ): String {
        val storeName = audit.store?.name ?: "-"
        val startedAtText = audit.startedAt?.toDisplayString()
        val completedAtText = audit.completedAt?.toDisplayString() ?: "Open"
        val createdBy = audit.createdBy ?: ""

        val rowsHtml = audit.items.joinToString("") { item ->
            val upc = item.sku?.upc.orEmpty()
            val description = item.sku?.name.orEmpty()
            val quantity = item.count
            val note = item.note.orEmpty()

            // We only generate a barcode if we have a non-blank UPC
            val barcodeBase64 = upc.takeIf { it.isNotBlank() }?.let {
                barcodeGenerator
                    .generate(it, widthPx = 600, heightPx = 200)
                    .toBase64Png()
            }

            val barcodeHtml = barcodeBase64?.let { base64 ->
                """
                    <div cass="barcode">
                        <img src="data:image/png;base64,$base64" alt="Barcode $upc" />
                    </div>
                """.trimIndent()
            } ?: ""

            """
                <tr>
                    <td>$upc</td>
                    <td>$description</td>
                    <td>$quantity</td>
                    <td>
                        $note
                        $barcodeHtml
                    </td>
                </tr>
            """.trimIndent()
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8" />
                <style>
                body { font-family: sans-serif; padding: 16px; }
                    h1 { font-size: 20px; margin-bottom: 8px; }
                    .meta { font-size: 12px; margin-bottom: 16px; }
                    table { width: 100%; border-collapse: collapse; font-size: 12px; }
                    th, td { border: 1px solid #444; padding: 4px; }
                    th { background: #eee; text-align: left; }
                    .barcode { margin-top: 4px; }
                    .barcode img { max-width: 200px; height: auto; }
                </style>
            </head>
            <body>
                <h1>Audit Report</h1>
                <div class="meta">
                    Store: ${storeName}<br />
                    Started: ${startedAtText}<br/>
                    Completed: ${completedAtText}<br/>
                    Created by: ${createdBy}<br/>
                </div>
                <table>
                    <tr>
                        <th>UPC</th>
                        <th>Description</th>
                        <th>Qty</th>
                        <th>Notes</th>
                    </tr>
                    $rowsHtml
                </table>
            </body>
            </html>
        """.trimIndent()
    }
}