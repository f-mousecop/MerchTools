package com.example.merchtools.domain.validation

/**
 * A singleton object that provides validation and sanitization functions for text input fields.
 *
 * This object contains utility methods to enforce constraints like maximum length and to clean up
 * user input, such as trimming trailing whitespace.
 */
object TextInputFieldValidator {
    // We need to limit the input length to 50 characters
    private const val MAX_LEN = 50

    /**
     * Caps the length of a user input string to a predefined maximum length.
     *
     * This function takes a string and returns a new string containing only the
     * initial characters up to `MAX_LEN`. If the input string is shorter than
     * `MAX_LEN`, the original string is returned.
     *
     * @param userInput The string to be capped.
     * @return The capped string, which will be at most `MAX_LEN` characters long.
     * @see MAX_LEN
     */
    fun capInputLength(userInput: String): String {
        // Return the first 50 characters of a string
        return userInput.take(MAX_LEN)
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
     * @see MAX_LEN
     */
    fun trimTrailingSpaces(userInput: String): String {
        // Return the trimmed string
        return userInput.trimEnd().take(MAX_LEN)
    }
}