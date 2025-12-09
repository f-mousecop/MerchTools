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


/**
 * A composable that wraps another composable to provide swipe-to-delete functionality.
 * When an item is swiped from end to start, a confirmation background is shown with "Delete" and "Cancel" options.
 * The deletion is animated with a vertical shrink and fade out.
 *
 * This composable uses the experimental `SwipeToDismissBox` from Material 3.
 *
 * @param T The type of the item being displayed.
 * @param item The data item that the container represents. This item will be passed to the `onDelete` callback.
 * @param onDelete A lambda function that is called when the delete action is confirmed. It receives the `item` to be deleted.
 * @param animationDuration The duration for the exit animation when an item is deleted. Defaults to 500 milliseconds.
 * @param content The composable content to be displayed inside the container. It receives the `item` to render.
 */
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

/**
 * A composable that serves as the background content for a `SwipeToDismissBox`.
 * It becomes visible when the user swipes an item from end to start.
 * The background animates to red and displays a "Delete?" prompt with two icons:
 * one to cancel the swipe action and another to confirm the deletion.
 *
 * @param swipeDismissState The state of the `SwipeToDismissBox` which controls the background's visibility and color.
 * @param onDelete A lambda function to be invoked when the user confirms the delete action by clicking the delete icon.
 * @param onCancel A lambda function to be invoked when the user cancels the swipe action by clicking the clear icon.
 */
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