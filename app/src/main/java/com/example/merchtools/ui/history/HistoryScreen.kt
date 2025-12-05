package com.example.merchtools.ui.history

import androidx.compose.animation.core.Animatable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun HistoryScreen(
    navigator: DestinationsNavigator
) {
    val alpha = remember { Animatable(initialValue = 1f) }

    LaunchedEffect(Unit) {

    }

    Text("Don't forget to save!", Modifier.alpha(alpha.value))

}
