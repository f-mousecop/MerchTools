package com.example.merchtools.domain.validation

/**
 * Provides validation logic and utility functions for validating input data
 * entered into OutlinedTextField components.
 *
 * This object typically contains methods to verify business rules, formatting,
 * and constraints required for text input fields across the application.
 */
object AuditItemValidator {
    /**
     * Limits the input string to a specified maximum length, preventing excessive input into an
     * [androidx.compose.material3.OutlinedTextField]
     */
    private const val MAX_NOTE_LENGTH = 120

    /**
     * Caps the length of a user input string to a predefined maximum length.
     *
     * This function takes a string and returns a new string containing only the
     * initial characters up to `MAX_LEN`. If the input string is shorter than
     * `MAX_LEN`, the original string is returned.
     *
     * @param userInput The string to be capped.
     * @return The capped string, which will be at most `MAX_LEN` characters long.
     * @see MAX_NOTE_LENGTH
     */
    fun capInputLength(userInput: String): String {
        return userInput.take(MAX_NOTE_LENGTH)
    }

    /**
     * Trims trailing whitespace from a user input string and then caps its length.
     *
     * This function first removes any whitespace characters (spaces, tabs, etc.) from the end
     * of the input string. After trimming, it ensures the resulting string does not exceed
     * the `MAX_LEN`.
     *
     * @param userInput The string to be trimmed and capped.
     * @return The cleaned and capped string.
     * @see MAX_NOTE_LENGTH
     */
    fun trimTrailingSpaces(userInput: String): String {
        return userInput.trimEnd().take(MAX_NOTE_LENGTH)
    }

    /**
     * Validates whether a count value for a specific [com.example.merchtools.domain.model.Sku] is non-negative.
     *
     * This function ensures that the provided integer is zero or greater, which is
     * typically required for inventory or item quantities.
     *
     * @param count The numeric value to validate.
     * @return `true` if the count is 0 or more, `false` otherwise.
     */
    fun isValidCount(count: Int): Boolean {
        return count >= 0
    }
}