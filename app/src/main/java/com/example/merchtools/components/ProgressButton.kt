package com.example.merchtools.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Composable button that displays a circular progress indicator when in a loading state.
 * It smoothly animates between its content and the progress indicator.
 *
 * @param isLoading A boolean indicating whether the button is in a loading state.
 *                  When true, a `CircularProgressIndicator` is shown.
 * @param enabled A boolean to control the enabled state of the button. The button is also
 *                disabled when `isLoading` is true.
 * @param onClick The lambda to be executed when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param content The composable content to be displayed inside the button when it's not loading
 *                (e.g., a `Text` composable).
 */
@Composable
fun ProgressButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.small
    ) {
        // AnimatedContent to smoothly transition between text and progress indicator
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "ButtonContentAnimation"
        ) { targetIsLoading ->
            if (targetIsLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                )
            } else {
                content()
            }
        }
    }
}