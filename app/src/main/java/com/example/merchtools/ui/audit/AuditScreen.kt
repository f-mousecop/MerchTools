package com.example.merchtools.ui.audit

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.ui.components.ProgressButton
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.ui.components.AuditInventoryItem
import com.example.merchtools.ui.components.SwipeToDeleteContainer
import com.example.merchtools.ui.feature_scanner.BarcodeScanResult
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.example.merchtools.core.toDisplayString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditAuditItemScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ScanBarCodeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun AuditScreen(
    auditId: Long,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<ScanBarCodeScreenDestination, BarcodeScanResult>,
    viewModel: AuditViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    resultRecipient.onNavResult { navResult ->
        when (navResult) {
            is NavResult.Value -> {
                val result = navResult.value
                viewModel.onEvent(AuditEvent.AddItemBySearch(result.upc))
            }
            NavResult.Canceled -> {

            }
        }
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is AuditUiEffect.NavigateToEditAuditItem -> {
                    navigator.navigate(EditAuditItemScreenDestination(effect.auditItemId))
                }
                is AuditUiEffect.NavigateToHistoryScreen -> {
                    navigator.navigate(HistoryScreenDestination)
                }
                is AuditUiEffect.NavigateToScanBarcode -> {
                    navigator.navigate(ScanBarCodeScreenDestination)
                }
                is AuditUiEffect.ShowMessage -> {
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
        AuditScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            barcodeGenerator = viewModel.barcodeGen,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AuditScreenContent(
    state: AuditState,
    onEvent: (AuditEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        AuditScreenBody(
            state = state,
            onEvent = onEvent,
            barcodeGenerator = barcodeGenerator,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        ProgressButton(
            isLoading = state.isLoading,
            enabled = true,
            onClick = { onEvent(AuditEvent.SaveAudit) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small)),
            content = { Text(stringResource(R.string.save_action)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreenBody(
    state: AuditState,
    onEvent: (AuditEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    var newAuditItem by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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

            val startedAtText = state.audit.startedAt
                ?.toDisplayString()
                ?: "—"
            Text(
                text = "Started at: $startedAtText",
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

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    if (newAuditItem.isNotBlank()) {
                        onEvent(AuditEvent.AddItemBySearch(newAuditItem))
                        newAuditItem = ""
                    } else {
                        onEvent(AuditEvent.AddNewItem)
                        newAuditItem = ""
                    }
                }
            ) {
                Text("Add Item")
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { onEvent(AuditEvent.BarcodeScanned) }
            ) {
                Text("Scan Barcode")
            }


        }
        OutlinedTextField(
            value = newAuditItem,
            onValueChange = { newText ->
                newAuditItem = newText
            },
            label = { Text("Enter or scan UPC") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
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
                    .fillMaxSize(),
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
                    // Enables swipe left to give the option to delete AuditItem
                    SwipeToDeleteContainer(
                        item = item,
                        onDelete = { onEvent(AuditEvent.RemoveItem(item)) }
                    ) {
                        // Navigate to edit audit item screen passing the auditItemId
                        AuditInventoryItem(
                            item = item,
                            onClick = { onEvent(AuditEvent.EditAuditItem(item.auditItemId)) },
                            barcodeGenerator = barcodeGenerator,
                            modifier = Modifier.fillMaxWidth(),
                            clickable = true,
                            height = 220.dp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuditScreenPreview() {
    MerchToolsTheme {
        /*AuditScreenContent(
            state = AuditState(),
            onEvent = {},

        )*/
    }
}