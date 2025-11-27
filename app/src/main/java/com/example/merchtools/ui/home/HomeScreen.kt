package com.example.merchtools.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.merchtools.R
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiEffect = viewModel.uiEffect
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
            is HomeUiEffect.NavigateToAudit -> {
                navigator.navigate(AuditScreenDestination(effect.auditId))
            }
            is HomeUiEffect.ShowMessage -> {
                snackbarHostState.showSnackbar(
                    message = effect.message
                )
            }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        HomeScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateToAuditHistory = {
                navigator.navigate(SearchScreenDestination())
            },
            modifier = Modifier.padding(innerPadding)
        )
    }



}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToAuditHistory: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onEvent(HomeEvent.StartAuditClicked) }
                ) {
                    Text(stringResource(R.string.start_audit))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onEvent(HomeEvent.OpenAuditClicked) }
                ) {
                    Text(stringResource(R.string.open_audit))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onNavigateToAuditHistory() }
                ) {
                    Text(stringResource(R.string.audit_history))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MerchToolsTheme {
        HomeScreenContent(
            state = HomeState(),
            onEvent = {},
            onNavigateToAuditHistory = {}
        )
    }
}