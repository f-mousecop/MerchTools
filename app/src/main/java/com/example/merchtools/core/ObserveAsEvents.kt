package com.example.merchtools.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * A Composable function that observes a [Flow] as a stream of one-time events.
 *
 * This function ensures that events are collected in a lifecycle-aware manner,
 * specifically when the lifecycle is at least in the [Lifecycle.State.STARTED] state.
 * It uses [Dispatchers.Main.immediate] to ensure events are handled on the main thread
 * without unnecessary delay.
 *
 * @param T The type of event being observed.
 * @param flow The [Flow] to be collected.
 * @param key1 An optional key to trigger a restart of the collection when changed.
 * @param key2 An optional key to trigger a restart of the collection when changed.
 * @param onEvent A lambda expression that is invoked for each emitted event.
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, flow, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}