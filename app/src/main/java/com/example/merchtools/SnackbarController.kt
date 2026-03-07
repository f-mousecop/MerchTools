package com.example.merchtools

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Represents a snackbar message event to be displayed in the UI.
 *
 * @property message The text content to be displayed in the snackbar.
 * @property action An optional [SnackbarAction] providing a clickable button and its associated behavior.
 */
data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
)

/**
 * Represents an action that can be performed from a snackbar notification.
 *
 * @property name The text label to be displayed on the snackbar action button.
 * @property action The asynchronous callback to be executed when the user interacts with the action button.
 */
data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

/**
 * A singleton controller responsible for managing and dispatching snackbar messages across the application.
 *
 * It uses a [Channel] to capture [SnackbarEvent]s and exposes them as a [Flow], allowing
 * UI components to observe and display notifications in response to application events.
 */
object SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}