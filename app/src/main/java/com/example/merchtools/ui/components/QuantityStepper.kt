package com.example.merchtools.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merchtools.R

/**
 * A Composable that allows users to increment, decrement, or manually input a quantity.
 * It consists of two buttons for incrementing and decrementing, and a text field
 * for direct input. The value is constrained by a minimum and maximum value.
 *
 * @param value The current quantity to be displayed.
 * @param modifier The modifier to be applied to the stepper row.
 * @param minValue The minimum allowed quantity. Defaults to 0.
 * @param maxValue The maximum allowed quantity. Defaults to 200.
 * @param onValueChange A callback that is invoked when the quantity changes, providing the new value.
 */
@Composable
fun QuantityStepper(
    value: Int,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 200,
    onValueChange: (Int) -> Unit
) {
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.count),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = modifier
                .wrapContentWidth()
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepperButton(
                icon = Icons.Default.Remove,
                contentDescription = stringResource(R.string.decrease_quantity),
                enabled = value > minValue
            ) {
                if (value > minValue) {
                    onValueChange(value - 1)
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = value.toString(),
                    onValueChange = { newValue ->
                        val parsed = newValue.toIntOrNull()
                        val newQuantity = when {
                            parsed == null -> minValue
                            parsed < minValue -> minValue
                            parsed > maxValue -> maxValue
                            else -> parsed
                        }
                        onValueChange(newQuantity)
                    },
                    placeholder = { Text(text = minValue.toString()) },
                    modifier = Modifier
                        .width(64.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = shape
                )
            }

            StepperButton(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.increase_quantity),
                enabled = value < maxValue
            ) {
                if (value < maxValue) {
                    onValueChange(value + 1)
                }
            }
        }
    }
}

/**
 * A private composable function that creates a styled button for a stepper control.
 * It's typically used for incrementing or decrementing a value.
 *
 * @param icon The [ImageVector] to be displayed on the button.
 * @param contentDescription A text description for accessibility services.
 * @param enabled A boolean to control if the button is enabled and clickable.
 * @param onClick The lambda function to be executed when the button is clicked.
 */
@Composable
private fun StepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (enabled)
            MaterialTheme.colorScheme.onSecondaryContainer
        else
            MaterialTheme.colorScheme.inverseOnSurface,
        tonalElevation = if (enabled) 2.dp else 0.dp
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuantityStepperPreview(
    modifier: Modifier = Modifier
) {
    QuantityStepper(
        value = 1,
        modifier = modifier,
        minValue = 0,
        maxValue = 200,
        onValueChange = {}
    )
}