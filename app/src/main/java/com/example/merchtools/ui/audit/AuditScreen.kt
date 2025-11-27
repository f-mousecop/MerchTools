package com.example.merchtools.ui.audit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination<RootGraph>
@Composable
fun AuditScreen(
    auditId: Long,
    navigator: DestinationsNavigator,
    viewModel: AuditViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AuditScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AuditScreenContent(
    state: AuditState,
    onEvent: (AuditEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { onEvent(AuditEvent.AddNewItem) }
            ) {
                Text("Add Item")
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { onEvent(AuditEvent.BarcodeScanned("123")) }
            ) {
                Text("Scan Barcode")
            }
        }

        AuditScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AuditScreenBody(
    state: AuditState,
    onEvent: (AuditEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var newAuditItem by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = newAuditItem,
            onValueChange = { newAuditItem = it },
            label = { Text("Enter or scan UPC") },
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_small)
                ),
                contentPadding = PaddingValues(
                    bottom = dimensionResource(R.dimen.padding_medium)
                ),
            ) {
                items(
                    items = state.audit.items
                ) { item ->
                    InventoryItem(
                        item = item,
                        modifier = Modifier.fillMaxWidth()
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
        AuditScreenContent(
            state = AuditState(),
            onEvent = {}
        )
    }
}