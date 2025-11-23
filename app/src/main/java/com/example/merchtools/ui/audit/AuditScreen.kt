package com.example.merchtools.ui.audit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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
    navigator: DestinationsNavigator,
    viewModel: AuditViewModel = hiltViewModel()
) {
     AuditScreenContent(
         navigator = navigator,
         state = viewModel.state
     )
}

@Composable
fun AuditScreenContent(
    navigator: DestinationsNavigator,
    state: AuditState
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
                onClick = { TODO() }
            ) {
                Text("Add Item")
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { TODO() }
            ) {
                Text("Scan Barcode")
            }
        }

        AuditScreenBody()
    }
}

@Composable
fun AuditScreenBody() {
    LazyColumn(
        modifier = Modifier
    ) {
        items(10) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = dimensionResource(R.dimen.padding_small)),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )

            ) {
                Text("hello")
                /*Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Hello")
                }*/

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuditScreenPreview() {
    MerchToolsTheme {
        AuditScreenContent(
            navigator = EmptyDestinationsNavigator,
            state = AuditState()
        )
    }
}