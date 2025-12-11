package com.example.merchtools.ui.report

import android.content.Context
import android.net.Uri

sealed class GenerateReportEvent {
    data class GeneratePdfClicked(
        val context: Context,
        val onResult: (Result<Uri>) -> Unit
    ) : GenerateReportEvent()
    data object NavigateBack : GenerateReportEvent()
}