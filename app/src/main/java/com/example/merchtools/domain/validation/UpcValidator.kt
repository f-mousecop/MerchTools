package com.example.merchtools.domain.validation

/**
 * A singleton object that provides validation logic for Universal Product Codes (UPCs).
 */
object UpcValidator {
    /**
     * Checks if a given string is a valid Universal Product Code (UPC).
     *
     * A valid UPC in this context is a string that consists of 12 or 13 digits.
     *
     * @param upc The UPC string to validate.
     * @return `true` if the UPC is a 12 or 13-digit number, `false` otherwise.
     */
    fun isValid(upc: String): Boolean {
        return upc.length in 12..13 && upc.all { it.isDigit() }
    }
}