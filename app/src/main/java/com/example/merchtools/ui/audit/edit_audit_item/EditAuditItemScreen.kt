package com.example.merchtools.ui.audit.edit_audit_item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.merchtools.R
import com.example.merchtools.SnackbarAction
import com.example.merchtools.SnackbarController
import com.example.merchtools.SnackbarEvent
import com.example.merchtools.core.ObserveAsEvents
import com.example.merchtools.ui.components.ProgressButton
import com.example.merchtools.ui.components.QuantityStepper
import com.example.merchtools.ui.components.SkuItemCard
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
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

    /*LaunchedEffect(Unit) {
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
    }*/

    /*
    * ObserveAsEvents is used in place of LaunchedEffect in every
    * screen. New snackbar controller from the navdrawer is used instead of
    * relying on manual snackbar launch from within launchedeffect
    *
    * Issue?: Now snackbar overlaps buttons, might be fine
    * TODO: See if there is a clean way to fix overlapping issue
    */
    ObserveAsEvents(flow = uiEffect) { effect ->
        when (effect) {
            is AuditItemUiEffect.NavigateUp -> {
                navigator.navigateUp()
            }
            is AuditItemUiEffect.ShowMessage -> {
                scope.launch {
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = effect.message,
                            action = SnackbarAction(
                                name = "Dismiss",
                                action = {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            )
                        )
                    )
                }
            }
        }
    }

    EditAuditItemScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .imePadding()
            .verticalScroll(rememberScrollState())
    )

    /*Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0)
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }*/
}

@Composable
fun EditAuditItemScreenContent(
    state: AuditItemState,
    onEvent: (AuditItemEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
    }
}

@Composable
fun AuditItemInputForm(
    state: AuditItemState,
    onValueChange: (AuditItemEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.sku),
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

        SkuItemCard(
            sku = state.auditItem.sku,
            clickable = false,
        )

        HorizontalDivider()

        Text(
            text = "Edit Count & Note",
            style = MaterialTheme.typography.headlineSmall,
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
                if (newText.lines().size <= 3) {
                    onValueChange(AuditItemEvent.OnNoteChanged(newText))
                }
            },
            maxLines = 3,
            supportingText = { Text("${state.auditItem.note?.length ?: 0}/120")},
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun EditAuditItemScreenPreview() {
    MerchToolsTheme {
        EditAuditItemScreenContent(
            state = AuditItemState(),
            onEvent = {},
        )
    }
}