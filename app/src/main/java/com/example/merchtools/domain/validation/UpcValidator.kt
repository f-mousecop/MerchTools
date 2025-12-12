package com.example.merchtools.domain.validation

object UpcValidator {
    fun isValid(upc: String): Boolean {
        return upc.length in 12..13 && upc.all { it.isDigit() }
    }
}