package com.example.merchtools.domain.validation

object TextInputFieldValidator {
    // We need to limit the input length to 50 characters
    private const val MAX_LEN = 50

    // Return the first 50 characters of a string
    fun capInputLength(userInput: String): String {
        return userInput.take(MAX_LEN)
    }

    // Return the trimmed string
    fun trimTrailingSpaces(userInput: String): String {
        return userInput.trimEnd().take(MAX_LEN)
    }
}