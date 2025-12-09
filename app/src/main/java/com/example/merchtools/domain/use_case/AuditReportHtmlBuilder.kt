package com.example.merchtools.domain.use_case

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.util.toBase64Png
import com.example.merchtools.util.toDisplayString
import jakarta.inject.Inject

class AuditReportHtmlBuilder @Inject constructor(){
    fun buildReportHtml(
        audit: Audit,
        barcodeGenerator: BarcodeGenerator
    ): String {
        val storeName = audit.store?.name ?: "-"
        val startedAtText = audit.startedAt?.toDisplayString() ?: ""
        val completedAtText = audit.completedAt?.toDisplayString() ?: "Open"
        val createdBy = audit.createdBy ?: ""

        val itemsHtml = audit.items.joinToString("") { item ->
            val upc = item.sku?.upc.orEmpty()
            val skuNumber = item.sku?.name.orEmpty()
            val brand = item.sku?.brand.orEmpty()
            val casePack = item.sku?.casePack.orEmpty()
            val quantity = item.count
            val note = item.note.orEmpty()

            val brandCase = listOf(brand, casePack)
                .filter { it.isNotBlank() }
                .joinToString(" / ")

            // We only generate a barcode if we have a non-blank UPC
            val barcodeBase64 = upc.takeIf { it.isNotBlank() }?.let {
                barcodeGenerator
                    .generate(it, widthPx = 400, heightPx = 120)
                    .toBase64Png()
            }

            val barcodeHtml = barcodeBase64?.let { base64 ->
        """
            <div class="card-barcode">
                <img src="data:image/png;base64,$base64" alt="Barcode $upc" />
            </div>
        """.trimIndent()
            } ?: ""

            """
                <div class="item-card">
                    $barcodeHtml
                    <div class="field-row">
                        <span class="field-label">UPC:</span>
                        <span class="field-value">$upc</span>
                    </div>
                    <div class="field-row">
                        <span class="field-label">SKU #:</span>
                        <span class="field-value">$skuNumber</span>
                    </div>
                    <div class="field-row">
                        <span class="field-label">Brand / CasePack:</span>
                        <span class="field-value">${brandCase.ifBlank { "-" }}</span>
                    </div>
                    <div class="field-row">
                        <span class="field-label">Count:</span>
                        <span class="field-value">$quantity</span>
                    </div>
                    <div class="field-row">
                        <span class="field-label">Note:</span>
                        <span class="field-value">${note.ifBlank { "-" }}</span>
                    </div>
                </div>
            """.trimIndent()
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8" />
                <style>
                body { 
                    font-family: sans-serif; 
                    padding: 16px; 
                }
                h1 { 
                    font-size: 20px; 
                    margin-bottom: 8px; 
                }
                .meta { 
                    font-size: 12px; 
                    margin-bottom: 16px; 
                }
                .items-container {
                        display: flex;
                        flex-direction: column;
                        gap: 12px;
                    }

                    .item-card {
                        border: 1px solid #444;
                        border-radius: 4px;
                        padding: 8px 10px;
                        margin-bottom: 8px;
                        page-break-inside: avoid;
                        break-inside: avoid;
                    }

                    .card-barcode {
                        text-align: center;
                        margin-bottom: 3rem;
                        margin-top: 3rem;
                    }
                    .card-barcode img {
                        display: block;
                        margin: 0 auto;
                        max-width: 100%;    
                        height: auto;
                        image-rendering: pixelated;
                    }

                    .field-row {
                        display: flex;
                        font-size: 12px;
                        margin-bottom: 2px;
                    }
                    .field-label {
                        font-weight: bold;
                        min-width: 110px;
                    }
                    .field-value {
                        flex: 1;
                    }
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
                <div class="items-container">
                    $itemsHtml
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}