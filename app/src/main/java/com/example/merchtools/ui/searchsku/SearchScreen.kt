package com.example.merchtools.ui.searchsku

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.ui.audit.AuditEvent
import com.example.merchtools.ui.components.SkuItemCard
import com.example.merchtools.ui.components.SwipeToDeleteContainer
import com.example.merchtools.ui.feature_scanner.BarcodeScanResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditSkuScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ScanBarCodeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<ScanBarCodeScreenDestination, BarcodeScanResult>,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    resultRecipient.onNavResult { navResult ->
        when (navResult) {
            is NavResult.Value -> {
                val result = navResult.value
                viewModel.onEvent(SearchSkuEvent.AddNewSku(result.upc))
            }
            NavResult.Canceled -> {

            }
        }
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is SearchSkuUiEffect.NavigateToSkuDetails -> {
                    navigator.navigate(
                        EditSkuScreenDestination(effect.skuId)
                    )
                }
                is SearchSkuUiEffect.NavigateToScanBarcode -> {
                    navigator.navigate(ScanBarCodeScreenDestination)
                }
                is SearchSkuUiEffect.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message
                        )
                    }
                }
                else -> {
                    return@collect
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->

        val state = viewModel.state

        // We need to remember lazyListState for scroll animation
        val listState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        viewModel.onEvent(SearchSkuEvent.AddNewSku(upc = ""))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text(text = "Add New SKU")
                }

                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        viewModel.onEvent(SearchSkuEvent.BarcodeScanned)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text(text = "Scan Barcode")
                }
            }

            /**
             * TODO: Fix scroll animation issue where LaunchedEffect is called on re-entry
             * of the composable causing scroll animation to jump to top of list
             * e.g., user scrolls down, clicks SKU item -> nav to edit SKU
             * user navs back -> scroll animation jumps to top of list, losing current index
             */
            // After search query or deletion, smoothly scroll to top
            // of the LazyColumn
            LaunchedEffect(state.searchQuery, state.skus.size) {
                listState. animateScrollToItem(0)
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(
                        SearchSkuEvent.OnSearchQueryChange(it)
                    )
                    /*scope.launch {
                        listState.animateScrollToItem(0)
                    }*/
                },
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search by UPC, name, or brand...")
                },
                maxLines = 1,
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                ),
                contentPadding = PaddingValues(
                    top = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_medium)
                )
                ) {
                    items(
                        items = state.skus,
                        key = { item ->
                            item.skuId
                        }
                    ) { item ->
                        SwipeToDeleteContainer(
                            item = item,
                            onDelete = { viewModel.onEvent(SearchSkuEvent.RemoveSku(item)) }
                        ) {
                            SkuItemCard(
                                sku = item,
                                onClick = {
                                    viewModel.onEvent(SearchSkuEvent.EditSku(item.skuId)) },
                                clickable = true,)
                        }
                    }
                }
        }
    }
}
