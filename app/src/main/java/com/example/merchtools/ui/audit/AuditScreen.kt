package com.example.merchtools.ui.audit

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.merchtools.R
import com.example.merchtools.core.toDisplayString
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.model.Store
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import java.time.Instant

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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val expanded by viewModel.expanded.collectAsStateWithLifecycle()

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
            uiState = uiState,
            onEvent = viewModel::onEvent,
            barcodeGenerator = viewModel.barcodeGen,
            searchQuery = searchQuery,
            searchResults = searchResults,
            expanded = expanded,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        )
    }
}

@Composable
fun AuditScreenContent(
    uiState: AuditState,
    onEvent: (AuditEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchResults: List<Sku>,
    expanded: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            )
    ) {
        AuditScreenBody(
            uiState = uiState,
            onEvent = onEvent,
            barcodeGenerator = barcodeGenerator,
            modifier = Modifier
                .fillMaxSize(),
            searchQuery = searchQuery,
            searchResults = searchResults,
            expanded = expanded
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreenBody(
    uiState: AuditState,
    onEvent: (AuditEvent) -> Unit,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchResults: List<Sku>,
    expanded: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                val storeName = uiState.audit.store?.name
                Text(
                    text = storeName ?: "-",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))

                val startedAtText = uiState.audit.startedAt
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

                val createdBy = uiState.audit.createdBy
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
                        onEvent(AuditEvent.AddItemBySearch(searchQuery))
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

            Spacer(Modifier.height(64.dp))

            if (uiState.audit.items.isEmpty()) {
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
                        items = uiState.audit.items,
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
                                height = 200.dp
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
        SkuSearchBar(
            onEvent = onEvent,
            uiState = uiState,
            searchQuery = searchQuery,
            searchResults = searchResults,
            expanded = expanded,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 112.dp)
        )

        ProgressButton(
            isLoading = uiState.isLoading,
            enabled = true,
            onClick = { onEvent(AuditEvent.SaveAudit) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            content = { Text(stringResource(R.string.save_action)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkuSearchBar(
    onEvent: (AuditEvent) -> Unit,
    uiState: AuditState,
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchResults: List<Sku>,
    expanded: Boolean
) {
    DockedSearchBar(
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                query = searchQuery,
                onQueryChange = { newValue ->
                    onEvent(AuditEvent.OnSearchQueryChanged(newValue))
                },
                onSearch = { onEvent(AuditEvent.OnSearchExpandedChanged(false)) },
                expanded = expanded,
                onExpandedChange = { onEvent(AuditEvent.OnSearchExpandedChanged(it)) },
                placeholder = { Text("Search SKUs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            onEvent(AuditEvent.OnSearchQueryChanged(""))
                        } else {
                            onEvent(AuditEvent.OnSearchExpandedChanged(false))
                        }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close search")
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = { onEvent(AuditEvent.OnSearchExpandedChanged(it)) },
        shape = MaterialTheme.shapes.extraSmall,
        colors = SearchBarDefaults.colors(
            dividerColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            items(searchResults) { result ->
                ListItem(
                    overlineContent = { Text(result.brand)},
                    headlineContent = { Text(result.upc) },
                    supportingContent = { result.casePack?.let { Text(it) } },
                    modifier = Modifier
                        .clickable {
                            onEvent(AuditEvent.OnSearchQueryChanged(result.upc, fromSelection = true))
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuditScreenContentPreview() {
    val sampleSku = Sku(
        skuId = 1L,
        upc = "123456789012",
        name = "Whole Milk",
        casePack = "12/1L",
        brand = "Dairy Farm"
    )

    val sampleAuditItem = AuditItem(
        auditItemId = 1L,
        count = 10,
        sku = sampleSku
    )

    val sampleStore = Store(
        storeId = 1L,
        name = "Sample Store",
        address = "123 Main St"
    )

    val sampleAudit = Audit(
        auditId = 1L,
        store = sampleStore,
        createdBy = "John Doe",
        items = listOf(sampleAuditItem),
        startedAt = Instant.now()
    )

    val sampleAuditState = AuditState(
        audit = sampleAudit
    )

    val fakeBarcodeGenerator = object : BarcodeGenerator {
        override fun generate(upc: String, widthPx: Int, heightPx: Int): Bitmap {
            return Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        }
    }

    MerchToolsTheme {
        AuditScreenContent(
            uiState = sampleAuditState,
            onEvent = {},
            barcodeGenerator = fakeBarcodeGenerator,
            searchQuery = "",
            searchResults = emptyList(),
            expanded = false
        )
    }
}
