package com.example.merchtools.ui.history

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.example.merchtools.R
import com.example.merchtools.domain.model.Audit
import com.example.merchtools.domain.model.Store
import com.example.merchtools.ui.components.SwipeToDeleteContainer
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.example.merchtools.util.toDisplayString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AuditScreenDestination
import com.ramcosta.composedestinations.generated.destinations.GenerateReportScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun HistoryScreen(
    navigator: DestinationsNavigator,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HistoryUiEffect.NavigateToAudit -> {
                    navigator.navigate(AuditScreenDestination(effect.auditId))
                }
                is HistoryUiEffect.NavigateToReportScreen -> {
                    navigator.navigate(GenerateReportScreenDestination(effect.auditId))
                }
                is HistoryUiEffect.ShowMessage -> {
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
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.secondary
    ) { innerPadding ->
        HistoryScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HistoryScreenContent(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        HistoryScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun HistoryScreenBody(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier
) {
    var historyText by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.audit_history),
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
                RoundedCornerShape(4.dp)
            ),
            thickness = 2.dp
        )

        // We need to check if the list of Audits is empty
        if (state.audits.isEmpty()) {
            historyText = stringResource(R.string.history_blank)
            Text(
                text = historyText,
                modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // If not empty, we need to partition the list into completed and open audits
            val (completedAudits, openAudits) = remember(state.audits) {
                state.audits.partition { it.completedAt != null }
            }

            // Now we can display the completed and open audits separately inside the
            // LazyColumn
            LazyColumn(
                modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                ),
                contentPadding = PaddingValues(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                )
            ) {
                if (completedAudits.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed Audits",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = dimensionResource(R.dimen.padding_medium),
                                    vertical = dimensionResource(R.dimen.padding_small)
                                )
                        )
                        HorizontalDivider(thickness = 2.dp)
                    }

                    items(
                        items = completedAudits,
                        key = { it.auditId }
                    ) { item ->
                        SwipeToDeleteContainer(
                            item = item,
                            onDelete = { onEvent(HistoryEvent.DeleteAudit(item)) }
                        ) {
                            AuditCard(
                                audit = item,
                                onEvent = onEvent,
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                if (openAudits.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
                        Text(
                            text = "Open Audits",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = dimensionResource(R.dimen.padding_medium),
                                    vertical = dimensionResource(R.dimen.padding_small)
                                )
                        )
                        HorizontalDivider(thickness = 2.dp)
                    }

                    items(
                        items = openAudits,
                        key = { it.auditId }
                    ) { item ->
                        SwipeToDeleteContainer(
                            item = item,
                            onDelete = { onEvent(HistoryEvent.DeleteAudit(item)) }
                        ) {
                            AuditCard(
                                audit = item,
                                onEvent = onEvent,
                                onClick = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditCard(
    audit: Audit,
    onEvent: (HistoryEvent) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = audit.store?.name ?: "–"
                )
                Spacer(modifier.weight(1f))

                audit.createdBy?.let {
                    Text(
                        text = "Created by: $it"
                    )
                }
            }

            Row(
                modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                )
            ) {
                Text(
                    text = audit.completedAt?.toDisplayString() ?: "–"
                )
                Text(
                    text = "Items: ${audit.items.size}"
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = modifier
                        .padding(4.dp)
                        .weight(0.5f),
                    onClick = { onEvent(HistoryEvent.OpenAuditClicked(audit.auditId)) },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Open")
                }
                Button(
                    modifier = modifier
                        .padding(4.dp)
                        .weight(0.5f),
                    onClick = { onEvent(HistoryEvent.ExportPdfClicked(audit.auditId)) },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.generate_report))
                }
            }
        }
    }
}

@Preview(
    name = "History Screen",
    group = "1",
    showBackground = true
)
@Composable
fun HistoryScreenPreview() {
    MerchToolsTheme() {
        HistoryScreenContent(
            state = HistoryState(),
            onEvent = {}
        )
    }
}

@Preview(
    name = "Audit Card",
    group = "2",
    showBackground = true
)
@Composable
fun AuditCardPreview() {
    MerchToolsTheme() {
        AuditCard(
            audit = Audit(
                auditId = 1,
                completedAt = _root_ide_package_.java.time.Instant.now(),
                createdBy = "John Doe",
                items = listOf(),
                store = Store(
                    storeId = 1,
                    name = "Test Store"
                )
            ),
            onEvent = {},
            onClick = {},
            modifier = Modifier
        )
    }
}
