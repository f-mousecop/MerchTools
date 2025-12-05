package com.example.merchtools.ui.searchsku

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.merchtools.ui.components.SwipeToDeleteContainer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditSkuScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is SearchSkuUiEffect.NavigateToSkuDetails -> {
                    navigator.navigate(
                        EditSkuScreenDestination(effect.skuId)
                    )
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

        val swipeRefreshState = rememberPullToRefreshState()
        val state = viewModel.state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Row(
                modifier = Modifier
//                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        viewModel.onEvent(SearchSkuEvent.AddNewSku)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add New SKU")
                }
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(
                        SearchSkuEvent.OnSearchQueryChange(it)
                    )
                },
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search")
                },
                maxLines = 1,
                singleLine = true
            )

            PullToRefreshBox(
                state = swipeRefreshState,
                isRefreshing = viewModel.state.isRefreshing,
                onRefresh = {
                    viewModel.onEvent(SearchSkuEvent.Refresh)
                }
            ) {
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
                        items = state.skus,
                        key = { item ->
                            item.skuId
                        }
                    ) { item ->
                        SwipeToDeleteContainer(
                            item = item,
                            onDelete = { viewModel.onEvent(SearchSkuEvent.RemoveSku(item)) }
                        ) {
                            SkuItem(
                                sku = item,
                                onClick = {
                                    viewModel.onEvent(SearchSkuEvent.EditSku(item.skuId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
