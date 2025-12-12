package com.example.merchtools.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.ui.components.UiElementRichToolTip
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiEffect = viewModel.uiEffect
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val state = viewModel.state

    val tooltipState = rememberTooltipState(
        isPersistent = true
    )

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToAudit -> {
                    navigator.navigate(AuditScreenDestination(effect.auditId))
                }

                is HomeUiEffect.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Long
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
            FloatingActionButton(
                modifier = Modifier,
                onClick = {
                    scope.launch {
                        tooltipState.show()
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                UiElementRichToolTip(
                    richTooltipSubheadText = stringResource(R.string.home_tooltip_sub),
                    richTooltipText = stringResource(R.string.home_tooltip_text),
                    modifier = Modifier,
                    tooltipState = tooltipState
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Show more information")
                }
            }
        }
    ) { innerPadding ->
        HomeScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateToAuditHistory = {
                navigator.navigate(HistoryScreenDestination())
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_audit_button"),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onEvent(HomeEvent.ShowDialogClicked) }
                ) {
                    Text(stringResource(R.string.start_audit))
                }

                if (state.showDialog) {
                    AlertDialog(
                        onDismissRequest = { onEvent(HomeEvent.DismissDialog) },
                        title = { Text(stringResource(R.string.start_audit)) },
                        text = {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                DropDownMenuContent(
                                    state = state,
                                    onEvent = onEvent
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onEvent(HomeEvent.StartAuditClicked)
                                },
                                enabled = state.userName.isNotBlank() && state.storeName.isNotBlank(),
                                modifier = Modifier.testTag("start_audit_ok_button")

                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { onEvent(HomeEvent.DismissDialog) },
                                modifier = Modifier.testTag("start_audit_cancel_button")
                            ) {
                                Text("Cancel")
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = stringResource(R.string.start_audit),
                                modifier = Modifier.size(48.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 4.dp
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_audit_button"),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onEvent(HomeEvent.OpenAuditClicked) },
                ) {
                    Text(stringResource(R.string.open_audit))
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("audit_history_button"),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onNavigateToAuditHistory() },
                ) {
                    Text(stringResource(R.string.audit_history))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        OutlinedTextField(
            value = state.userName,
            onValueChange = { newName ->
                onEvent(HomeEvent.OnUserNameChanged(newName)
                )
            },
            label = { Text("Created by") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = state.isExpanded,
            onExpandedChange = { onEvent(HomeEvent.ExpandStoreMenu) },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.storeName,
                onValueChange = { },
                label = { Text("Store Name") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(state.isExpanded)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = state.isExpanded,
                onDismissRequest = { onEvent(HomeEvent.CloseStoreMenu) }
            ) {
                state.stores.forEach { store ->
                    DropdownMenuItem(
                        text = { Text(store.name) },
                        onClick = {
                            onEvent(HomeEvent.OnStoreNameChanged(store.name, store.storeId))
                            onEvent(HomeEvent.CloseStoreMenu)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
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