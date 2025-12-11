package com.example.merchtools.ui.report

import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Report


/**
 * Represents the state of the generate report screen.
 *
 * This data class holds all the necessary information to render the UI for generating a report,
 * including the current audit details, the generated report, loading status, and any potential errors.
 *
 * @property audit The current [Audit] object being used to generate the report. Defaults to an empty Audit.
 * @property report The generated [Report] object. Null if no report has been generated yet.
 * @property isLoading A boolean flag indicating if a report generation is currently in progress.
 * @property error A string containing an error message if the report generation failed, otherwise null.
 */
data class GenerateReportState(
    val audit: Audit = Audit(),
    val report: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
