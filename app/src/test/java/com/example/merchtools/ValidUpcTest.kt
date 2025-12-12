package com.example.merchtools

import com.example.merchtools.domain.validation.UpcValidator
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidUpcTest {
    @Test
    fun `12 digit UPC is valid`() {
        assertTrue(UpcValidator.isValid("123456789012"))
    }

    @Test
    fun `13 digit UPC is valid`() {
        assertTrue(UpcValidator.isValid("1234567890123"))
    }

    @Test
    fun `non-digit UPC is invalid`() {
        assertFalse(UpcValidator.isValid("123asda12311"))
    }

    @Test
    fun `too short UPC is invalid`() {
        assertFalse(UpcValidator.isValid("123"))
    }
}