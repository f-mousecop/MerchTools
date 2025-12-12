package com.example.merchtools.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiElementRichToolTip(
    modifier: Modifier = Modifier,
    richTooltipSubheadText: String?,
    richTooltipText: String?,
    tooltipState: TooltipState,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above,
            spacingBetweenTooltipAndAnchor = 8.dp
        ),
        tooltip = {
            RichTooltip(
                title = { Text(richTooltipSubheadText ?: "") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(richTooltipText ?: "")
            }
        },
        state = tooltipState,
        content = content
    )
}