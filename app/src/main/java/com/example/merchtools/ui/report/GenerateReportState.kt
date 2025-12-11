package com.example.merchtools.ui.report

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Report

/**
 * Represents the state for the report generation screen.
 *
 * This data class holds all the necessary information to render the UI for generating a report,
 * including the audit data, the generated HTML content, loading status, and any potential errors.
 *
 * @property audit The [Audit] object containing the data to be included in the report. Defaults to an empty audit.
 * @property html The generated report content as an HTML string. This is null until the report is successfully generated.
 * @property isLoading A boolean flag indicating whether a report generation process is currently in progress.
 * @property error A string containing an error message if the report generation fails. This is null if there is no error.
 */
data class GenerateReportState(
    val audit: Audit = Audit(),
    val report: Report = Report(),
    val html: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
