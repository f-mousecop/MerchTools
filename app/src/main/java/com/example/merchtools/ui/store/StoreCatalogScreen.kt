package com.example.merchtools.ui.store

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.merchtools.R
import com.example.merchtools.domain.model.Store
import com.example.merchtools.ui.components.UiElementRichToolTip
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun StoreCatalogScreen(
    navigator: DestinationsNavigator,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()

    /**
     * TODO: Maybe extract this into a composable function that can be used across all screens?
     */
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            uiEffect.collect { effect ->
                when (effect) {
                    is StoreCatalogUiEffect.ShowMessage -> {
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
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        floatingActionButton = {
            UiElementRichToolTip(
                richTooltipSubheadText = stringResource(R.string.store_tooltip_sub),
                richTooltipText = stringResource(R.string.store_tooltip_text),
                tooltipState = rememberTooltipState(isPersistent = true)
            ) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(StoreCatalogEvent.ShowAddStoreDialog) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        StoreCatalogScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )

        /**
         * TODO: Need to fix bug where when keyboard is open and user rotates device, the keyboard glitches instead of gracefully closing
         * I believe this is the problem child, conditionally calling and displaying the dialog in
         * in the scaffold. Maybe should be moved to StoreCatalogBody
         */
        if (state.isAddStoreDialogOpen) {
            AddStoreDialog(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
fun StoreCatalogScreenContent(
    state: StoreState,
    onEvent: (StoreCatalogEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        StoreCatalogScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun StoreCatalogScreenBody(
    state: StoreState,
    onEvent: (StoreCatalogEvent) -> Unit,
    modifier: Modifier
) {
    var storeText by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.store_catalog),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(
            Modifier.border(
                2.dp,
                Brush.horizontalGradient(
                    0.0f to Color.Black,
                    1.0f to MaterialTheme.colorScheme.outline,
                    startX = 0.0f,
                    endX = 100.0f
                ),
                RoundedCornerShape(12.dp)
            ),
            thickness = 2.dp
        )

        if (state.stores.isEmpty()) {
            storeText = stringResource(R.string.store_catalog_blank)
            Text(
                text = storeText,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
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
                )
            ) {
                items(
                    items = state.stores,
                    key = { item ->
                        item.storeId
                    }
                ) { item ->
                    StoreCard(
                        store = item,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun StoreCard(
    store: Store,
    onEvent: (StoreCatalogEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Store: ${store.name}",
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onEvent(StoreCatalogEvent.RemoveStore(store)) },
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete store"
                    )
                }
            }

            val storeAddress = store.address ?: "–"
            Text(
                text = "Address: $storeAddress"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoreDialog(
    state: StoreState,
    onEvent: (StoreCatalogEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onEvent(StoreCatalogEvent.HideAddStoreDialog) },
        title = { Text("Add Store") },
        text = {
            OutlinedTextField(
                value = state.newStoreName,
                onValueChange = { newName -> onEvent(StoreCatalogEvent.OnStoreNameChanged(newName)) },
                label = { Text("Store name") },
                supportingText = { Text("${state.newStoreName.length}/50") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onEvent(StoreCatalogEvent.AddNewStore)
            },
                enabled = state.newStoreName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(StoreCatalogEvent.HideAddStoreDialog) }
            ) {
                Text("Cancel")
            }
        },
        icon = {
            Icon(
                Icons.Default.Store,
                contentDescription = "Add store",
                modifier = Modifier.size(48.dp)
            )
        },
        shape = RoundedCornerShape(8.dp)
    )
}

@Preview(
    name = "Store Catalog Screen",
    group = "1",
    showBackground = true
)
@Composable
fun StoreCatalogScreenPreview() {
    MerchToolsTheme() {
        StoreCatalogScreenContent(
            state = StoreState(),
            onEvent = {}
        )
    }
}

@Preview(
    name = "Store Card",
    group = "2",
    showBackground = true
)
@Composable
fun StoreCardPreview() {
    MerchToolsTheme() {
        StoreCard(
            onEvent = {},
            store = Store(
                storeId = 1,
                name = "Test Store",
                address = "123 Abbot Ln"
            )
        )
    }
}