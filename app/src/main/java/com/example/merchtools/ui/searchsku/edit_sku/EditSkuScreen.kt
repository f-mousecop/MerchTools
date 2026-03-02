package com.example.merchtools.ui.searchsku.edit_sku

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.merchtools.R
import com.example.merchtools.ui.components.ProgressButton
import com.example.merchtools.ui.searchsku.SearchSkuUiEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun EditSkuScreen(
    skuId: Long,
    navigator: DestinationsNavigator,
    viewModel: EditSkuViewModel = hiltViewModel()
) {
    val uiEffect = viewModel.uiEffect
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.state

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is SearchSkuUiEffect.NavigateUp -> {
                    navigator.navigateUp()
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
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        EditSkuScreenContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        )
    }
}

@Composable
fun EditSkuScreenContent(
    state: EditSkuState,
    onEvent: (EditSkuEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
    ) {
        EditSkuInputForm(
            state = state,
            onValueChange = onEvent,
            modifier = Modifier.fillMaxWidth()
        )
        ProgressButton(
            isLoading = state.isLoading,
            enabled = state.isEntryValid,
            onClick = { onEvent(EditSkuEvent.SaveSku) },
            modifier = Modifier,
            content = { Text(stringResource(R.string.save_action)) }
        )
    }
}

@Composable
fun EditSkuInputForm(
    state: EditSkuState,
    onValueChange: (EditSkuEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            onValueChange(EditSkuEvent.OnImageUriChanged(uri))
        }
    }


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

        OutlinedTextField(
            value = state.sku.upc,
            enabled = true,
            label = { Text(stringResource(R.string.upc)) },
            placeholder = { Text("12 or 13 digit UPC number")},
            supportingText = { Text(stringResource(R.string.invalid_upc)) },
            onValueChange = { newValue ->
                onValueChange(EditSkuEvent.OnUpcChanged(newValue))
            },
            isError = !state.isUpcValid,
            trailingIcon = @Composable {
                if (!state.isUpcValid) Icon(Icons.Filled.Error, "error")
                else {
                    Icon(Icons.Filled.Check, contentDescription = "valid")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.secondary,
                disabledLabelColor = MaterialTheme.colorScheme.secondary
            )
        )

        OutlinedTextField(
            value = state.sku.name,
            enabled = true,
            label = { Text(stringResource(R.string.sku_number)) },
            onValueChange = { newText ->
                onValueChange(EditSkuEvent.OnNameChanged(newText))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = !state.isEntryValid,
            trailingIcon = @Composable {
                if (!state.isEntryValid) Icon(Icons.Filled.Error, "error")
                else {
                    Icon(Icons.Filled.Check, contentDescription = "valid")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )


        OutlinedTextField(
            value = state.sku.brand,
            enabled = true,
            label = { Text(stringResource(R.string.brand)) },
            onValueChange = { newText ->
                onValueChange(EditSkuEvent.OnBrandChanged(newText))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            isError = !state.isEntryValid,
            trailingIcon = @Composable {
                if (!state.isEntryValid) Icon(Icons.Filled.Error, "error")
                else {
                    Icon(Icons.Filled.Check, contentDescription = "valid")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )

        OutlinedTextField(
            value = state.sku.casePack.orEmpty(),
            enabled = true,
            label = { Text(stringResource(R.string.case_pack)) },
            onValueChange = { newText ->
                onValueChange(EditSkuEvent.OnCasePackChanged(newText))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Button(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(0.5f)
            ) {
                Text("Add Product Image")
            }

            Button(
                onClick = {
                    onValueChange(EditSkuEvent.DiscardImageUri)
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(0.5f)
            ) {
                Text("Cancel")
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(240.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(4.dp),

        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.sku.imageUri)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.photo_240dp_placeholder),
                    error = painterResource(R.drawable.photo_240dp_placeholder),
                    contentDescription = "Produce image for ${state.sku.upc}",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    modifier = Modifier
                )
            }
        }
    }
}