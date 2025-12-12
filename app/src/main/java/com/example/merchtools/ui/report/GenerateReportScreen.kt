package com.example.merchtools.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.ui.components.ProgressButton
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.ui.components.AuditInventoryItem
import com.example.merchtools.util.sharePdfReport
import com.example.merchtools.util.toDisplayString
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
            ReportScreenContent(
                state = state,
                onEvent = viewModel::onEvent,
                barcodeGenerator = viewModel.barcodeGen,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
    }

}

@Composable
fun ReportScreenContent(
    state: GenerateReportState,
    onEvent: (GenerateReportEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                onClick = { onEvent(GenerateReportEvent.NavigateBack) }
            ) {
                Text("Back")
            }
            ProgressButton(
                isLoading = state.isLoading,
                enabled = true,
                onClick = {
                    onEvent(GenerateReportEvent.GeneratePdfClicked(context) { result ->
                        result.onSuccess { uri ->
                            sharePdfReport(context, uri, state.audit)
                        }
                    })
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = dimensionResource(R.dimen.padding_small)),
                content = { Text("Export PDF") }
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
