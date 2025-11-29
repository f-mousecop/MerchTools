package com.example.merchtools.ui.audit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.components.ProgressButton
import com.example.merchtools.components.QuantityStepper
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun EditAuditItemScreen(
    auditItemId: Long,
    navigator: DestinationsNavigator,
    viewModel: EditAuditItemViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is AuditItemUiEffect.NavigateUp -> {
                    navigator.navigateUp()
                }
                is AuditItemUiEffect.ShowMessage -> {
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        EditAuditItemScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EditAuditItemScreenContent(
    state: AuditItemState,
    onEvent: (AuditItemEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
    ) {
        AuditItemInputForm(
            state = state,
            onValueChange = onEvent,
            modifier = Modifier.fillMaxWidth()
        )
        ProgressButton(
            isLoading = state.isLoading,
            enabled = state.isEntryValid,
            onClick = { onEvent(AuditItemEvent.SaveAuditItem) },
            modifier = Modifier,
            content = { Text(stringResource(R.string.save_action)) }
        )
        /*Button(
            onClick = { onEvent(AuditItemEvent.SaveAuditItem) },
            enabled = state.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_action))
        }*/
    }
}

@Composable
fun AuditItemInputForm(
    state: AuditItemState,
    onValueChange: (AuditItemEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val sku = state.auditItem.sku
    val isUpcEditable = sku == null || sku.skuId == 0L
    val upc = sku?.upc.orEmpty()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.sku),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()

        OutlinedTextField(
            value = upc,
            enabled = isUpcEditable,
            readOnly = !isUpcEditable,
            label = { Text(stringResource(R.string.upc)) },
            onValueChange = { newValue ->
                if (isUpcEditable) {
                    onValueChange(AuditItemEvent.OnItemFieldChanged(newValue))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.auditItem.sku?.name ?: "",
            enabled = true,
            label = { Text(stringResource(R.string.item_name)) },
            onValueChange = { newText ->
                onValueChange(AuditItemEvent.OnNameChanged(newText))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.auditItem.sku?.casePack.orEmpty(),
            enabled = true,
            label = { Text(stringResource(R.string.case_pack)) },
            onValueChange = { newText ->
                onValueChange(AuditItemEvent.OnCasePackChanged(newText))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.auditItem.sku?.brand ?: "",
            enabled = true,
            label = { Text(stringResource(R.string.brand)) },
            onValueChange = { newText ->
                onValueChange(AuditItemEvent.OnBrandChanged(newText))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        HorizontalDivider()

        Text(
            text = "Edit Details",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()

        QuantityStepper(
            value = state.auditItem.count,
            onValueChange = { newCount ->
                onValueChange(AuditItemEvent.OnCountChanged(newCount))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.auditItem.note ?: "",
            enabled = true,
            label = { Text(stringResource(R.string.item_notes)) },
            onValueChange = { newText ->
                onValueChange(AuditItemEvent.OnNoteChanged(newText))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            Button(
                onClick = { onValueChange(AuditItemEvent.AddPhotoToItem) },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.add_photo))
            }

            Button(
                onClick = { onValueChange(AuditItemEvent.RemovePhotoFromItem) },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.remove_photo))
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                modifier = Modifier
                    .size(300.dp),
                painter = painterResource(R.drawable.pepsi_12pk),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditAuditItemScreenPreview() {
    MerchToolsTheme() {
        EditAuditItemScreenContent(
            state = AuditItemState(),
            onEvent = {}
        )
    }
}