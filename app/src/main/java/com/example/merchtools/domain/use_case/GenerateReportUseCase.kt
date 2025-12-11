package com.example.merchtools.domain.use_case

import android.print.PrintDocumentAdapter
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Report
import com.example.merchtools.domain.model.ReportItemRow
import com.example.merchtools.util.toDisplayString
import jakarta.inject.Inject

/**
 * Need to modify this use case so that it invokes an AuditReportPrintAdapter
 * @return [PrintDocumentAdapter]
 */
class GenerateReportUseCase @Inject constructor() {

    operator fun invoke(audit: Audit): Report {
        val rows = audit.items.mapIndexed { index, item ->
            val sku = item.sku

            val brand = sku?.brand?.takeUnless { it.isBlank() }
            val casePack = sku?.casePack?.takeUnless { it.isBlank() }

            val brandInfo = listOfNotNull(brand, casePack)
                .joinToString(" ")
                .ifBlank { "-" }

            ReportItemRow(
                index = index + 1,
                upc = sku?.upc?.takeUnless { it.isBlank() } ?: "-",
                skuName = sku?.name ?: "-",
                brandInfo = brandInfo,
                count = item.count,
                note = item.note ?: "-"
            )
        }

        return Report(
            auditId = audit.auditId,
            storeName = audit.store?.name ?: "-",
            createdBy = audit.createdBy ?: "-",
            startedAt = audit.startedAt?.toDisplayString() ?: "-",
            completedAt = audit.completedAt?.toDisplayString() ?: "-",
            items = rows
        )
    }
}
