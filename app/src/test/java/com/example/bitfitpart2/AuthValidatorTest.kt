package com.example.bitfitpart2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun invalidCredentials_blankEmailAndShortPassword_returnsErrors() {
        val emailError = AuthValidator.validateEmail("")
        val passwordError = AuthValidator.validatePassword("123")

        assertEquals(AuthValidator.EmailError.REQUIRED, emailError)
        assertEquals(AuthValidator.PasswordError.TOO_SHORT, passwordError)
    }

    @Test
    fun invalidEmail_missingAtSymbol_returnsInvalidFormat() {
        val emailError = AuthValidator.validateEmail("not-an-email")

        assertEquals(AuthValidator.EmailError.INVALID_FORMAT, emailError)
    }

    @Test
    fun validCredentials_wellFormedEmailAndLongEnoughPassword_returnsNoErrors() {
        val emailError = AuthValidator.validateEmail("user@example.com")
        val passwordError = AuthValidator.validatePassword("securePass123")

        assertNull(emailError)
        assertNull(passwordError)
    }
}
