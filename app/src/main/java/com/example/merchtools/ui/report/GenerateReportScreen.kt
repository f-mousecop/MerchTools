package com.example.merchtools.ui.report

import android.content.Context
import android.content.Intent
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.components.ProgressButton
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.ui.components.AuditInventoryItem
import com.example.merchtools.util.generateAuditPdfReport
import com.example.merchtools.util.sharePdfReport
import com.example.merchtools.util.toDisplayString
import com.google.firebase.components.BuildConfig
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun GenerateReportScreen(
    auditId: Long,
    navigator: DestinationsNavigator,
    viewModel: GenerateReportViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is GenerateReportUiEffect.NavigateBack -> {
                    navigator.navigateUp()
                }
                is GenerateReportUiEffect.GeneratePdf -> {
                    val printManager =
                        context.getSystemService(Context.PRINT_SERVICE) as PrintManager

                    val jobName = "${context.packageName}_audit_${state.audit.auditId}"

                    val adapter = viewModel.buildPrintAdapter(context) { result ->
                        result.onSuccess { uri ->
                            sharePdfReport(context, uri, state.audit)
                        }.onFailure { error ->
                            Toast.makeText(context, error.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    printManager.print(jobName, adapter, null)
                }
                is GenerateReportUiEffect.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        Box(
            modifier = Modifier
        ) {
            when {
                state.isLoading && state.audit.items.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                state.html != null -> {
                    RenderHtmlInWebView(
                        htmlContent = state.html,
                        state = state
                    )
                }

                else -> {
                    ReportScreenContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        barcodeGenerator = viewModel.barcodeGen,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

@Composable
fun ReportScreenContent(
    state: GenerateReportState,
    onEvent: (GenerateReportEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        ReportScreenBody(
            state = state,
            onEvent = onEvent,
            barcodeGenerator = barcodeGenerator,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { onEvent(GenerateReportEvent.NavigateBack) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Back")
            }
            ProgressButton(
                isLoading = state.isLoading,
                enabled = true,
                onClick = { onEvent(GenerateReportEvent.GeneratePdfClicked) },
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = dimensionResource(R.dimen.padding_small)),
                content = { Text("Generate PDF") }
            )
        }
    }
}

@Composable
fun ReportScreenBody(
    state: GenerateReportState,
    onEvent: (GenerateReportEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // This works fine
            val storeName = state.audit.store?.name
            Text(
                text = storeName ?: "-",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.weight(1f))

            val completedAtText = state.audit.completedAt
                ?.toDisplayString()
                ?: "—"
            Text(
                text = "Completed at: $completedAtText",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = "Created by: ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val createdBy = state.audit.createdBy
            Text(
                text = createdBy ?: "—",
                style = MaterialTheme.typography.titleMedium
            )
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small))
        )

        if (state.audit.items.isEmpty()) {
            Text(
                text = "No audit items",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                ),
                contentPadding = PaddingValues(
                    top = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_medium)
                ),
            ) {
                items(
                    items = state.audit.items,
                    key = { item ->
                        item.auditItemId
                    }
                ) { item ->
                    AuditInventoryItem(
                        item = item,
                        barcodeGenerator = barcodeGenerator,
                        modifier = Modifier.fillMaxWidth(),
                        clickable = false,
                        height = 240.dp
                    )
                }
            }
        }
    }
}

@Composable
fun RenderHtmlInWebView(
    htmlContent: String,
    state: GenerateReportState
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL(
                            null,
                            htmlContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                update = { view ->
                    webView = view
                    view.loadDataWithBaseURL(
                        null,
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                onClick = {
                    generateAuditPdfReport(
                        context = context,
                        webView = webView!!,
                        audit = state.audit
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(0.5f)
            ) {
                Text(stringResource(R.string.export_pdf))
            }
            Button(
                onClick = {
                    generateAuditPdfReport(
                        context = context,
                        webView = webView!!,
                        audit = state.audit
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(0.5f)
            ) {
                Text(stringResource(R.string.share_report))
            }
        }
    }
}
