package com.example.merchtools.util

import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.merchtools.domain.model.Audit
import com.google.android.datatransport.backend.cct.BuildConfig
import java.io.File

fun sharePdfReport(
    context: Context,
    audit: Audit,
    pdfFile: File
) {
    val authority = context.packageName + ".fileprovider"
    println("DEBUG FileProvider authority: $authority")
    val fileUri: Uri = FileProvider.getUriForFile(
        context,
        authority,
        pdfFile
    )

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_SUBJECT, "Audit Report: ${audit.store?.name ?: "Unknown store"}")
        putExtra(Intent.EXTRA_STREAM, fileUri)
        putExtra(
            Intent.EXTRA_TEXT,
            "Please find the attached audit report for ${audit.store?.name ?: "this store"}."
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooserTitle = "Share Audit Report via..."
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)

    context.startActivity(shareIntent)
    /*val fileUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        pdfFile
    )
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_SUBJECT, "Audit Report: ${audit.store?.name}")
        putExtra(Intent.EXTRA_STREAM, fileUri)
        putExtra(Intent.EXTRA_TEXT, "Please find the attached audit report for ${audit.store?.name}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooserTitle = "Share Audit Report via..."
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)

    context.startActivity(shareIntent)*/
}