package com.example.merchtools.ui.audit

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.core.toDisplayString
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.ui.components.AuditInventoryItem
import com.example.merchtools.ui.components.ProgressButton
import com.example.merchtools.ui.components.SwipeToDeleteContainer
import com.example.merchtools.ui.feature_scanner.BarcodeScanResult
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditAuditItemScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ScanBarCodeScreenDestination
import com.ramcosta.composedestinations.generated.navtype.barcodeScanResultNavType
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest
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
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state
    val textFieldState = TextFieldState()

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
                    launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        AuditScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            barcodeGenerator = viewModel.barcodeGen,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
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
            .padding(horizontal = dimensionResource(R.dimen.padding_medium), vertical = dimensionResource(R.dimen.padding_small))
    ) {
        AuditScreenBody(
            state = state,
            onEvent = onEvent,
            barcodeGenerator = barcodeGenerator,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
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
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Right
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    onEvent(AuditEvent.AddItemBySearch(newAuditItem))
                    if (newAuditItem.isNotBlank()) newAuditItem = ""
                }
            ) {
                Text("Add Item")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
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
        /*Spacer(Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)))
        val textFieldState = TextFieldState()
        SkuSearchBar(
            textFieldState = textFieldState,
            onEvent = onEvent,
            state = state,
        )*/

        if (state.audit.items.isEmpty()) {
            Text(
                text = "No audit items",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                ),
                contentPadding = PaddingValues(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
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

        ProgressButton(
            isLoading = state.isLoading,
            enabled = true,
            onClick = { onEvent(AuditEvent.SaveAudit) },
            modifier = Modifier
                .fillMaxWidth(),
            content = { Text(stringResource(R.string.save_action)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkuSearchBar(
    textFieldState: TextFieldState,
    onEvent: (AuditEvent) -> Unit,
    state: AuditState,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
//            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it ) } },
                    onSearch = {
                        onEvent(AuditEvent.OnSearch(query = textFieldState.text.toString()))
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                state.searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result.upc) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result.upc) }
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
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
            barcodeGenerator = null,
            modifier = Modifier,
        )*/
    }
}