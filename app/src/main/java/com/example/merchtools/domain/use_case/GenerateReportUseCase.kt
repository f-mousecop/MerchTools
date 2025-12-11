package com.example.merchtools.domain.use_case

import android.print.PrintDocumentAdapter
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Report
import com.example.merchtools.domain.model.ReportItemRow
import com.example.merchtools.util.toDisplayString
import jakarta.inject.Inject

/**
 * A use case responsible for generating a [Report] from an [Audit] object.
 *
 * This class transforms the raw audit data into a more presentable format, suitable for display
 * or for sharing via an intent/share sheet. It processes each item in the audit, formats its
 * details like SKU, brand, and case pack, and constructs a [Report] object containing
 * summarized information and a list of [ReportItemRow]s.
 *
 * @constructor Injected constructor for dependency injection.
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
