package com.example.merchtools.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.merchtools.R
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Duration = 500.milliseconds,
    content: @Composable (T) -> Unit
) {
    var shouldDelete by remember {
        mutableStateOf(false)
    }

    var shouldCancel by remember {
        mutableStateOf(false)
    }

    val state = rememberSwipeToDismissBoxState(
        positionalThreshold = {
            600f
        }
    )

    LaunchedEffect(shouldDelete) {
        if (shouldDelete) {
            delay(animationDuration)
            onDelete(item)
            shouldDelete = false
        }
    }

    LaunchedEffect(shouldCancel) {
        if (shouldCancel) {
            state.reset()
            shouldCancel = false
        }
    }

    AnimatedVisibility(
        visible = !shouldDelete,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration.toInt(DurationUnit.MILLISECONDS)),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                DeleteBackground(
                    swipeDismissState = state,
                    onDelete = {
                        shouldDelete = true
                    },
                    onCancel = {
                        shouldCancel = true
                    }
                )
            },
            onDismiss = { SwipeToDismissBoxValue.EndToStart },
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = false
        ) {
            content(item)
        }
    }
}

@Composable
fun DeleteBackground(
    swipeDismissState: SwipeToDismissBoxState,
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val targetColor = when (swipeDismissState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        else -> Color.Transparent
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        label = "SwipeBackgroundAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(dimensionResource(R.dimen.padding_medium)),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row {
            Text(
                text = "Delete?",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onError,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(20.dp))

            Icon(
                Icons.Default.Clear,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onCancel()
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onDelete()
                }
            )
        }
    }
}