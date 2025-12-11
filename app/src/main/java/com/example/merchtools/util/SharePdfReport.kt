package com.example.merchtools.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.merchtools.domain.model.Audit

fun sharePdfReport(
    context: Context,
    uri: Uri,
    audit: Audit
) {

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_SUBJECT, "Audit Report: ${audit.store?.name ?: "Unknown store"}")
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_TEXT,
            "Please find the attached audit report for ${audit.store?.name ?: "this store"}."
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooserTitle = "Share Audit Report via..."
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)

    context.startActivity(shareIntent)
}