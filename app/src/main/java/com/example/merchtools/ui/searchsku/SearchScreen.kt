package com.example.merchtools.ui.searchsku

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel()
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->

        val swipeRefreshState = rememberPullToRefreshState()
        val state = viewModel.state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    viewModel.onEvent(
                        SearchSkuEvent.OnSearchQueryChange(it)
                    )
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search")
                },
                maxLines = 1,
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            PullToRefreshBox(
                state = swipeRefreshState,
                isRefreshing = viewModel.state.isRefreshing,
                onRefresh = {
                    viewModel.onEvent(SearchSkuEvent.Refresh)
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.skus.size) { i ->
                        val sku = state.skus[i]
                        SkuItem(
                            sku = sku,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // TODO navigate to SKU details
                                }
                                .padding(16.dp)
                        )
                        if (i < state.skus.size) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .height(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
