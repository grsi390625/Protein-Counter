package com.example.bitfitpart2

object AuthValidator {
    const val MIN_PASSWORD_LENGTH = 6
    private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    enum class EmailError {
        REQUIRED,
        INVALID_FORMAT
    }

    enum class PasswordError {
        REQUIRED,
        TOO_SHORT
    }

    fun validateEmail(email: String): EmailError? {
        val trimmed = email.trim()
        return when {
            trimmed.isBlank() -> EmailError.REQUIRED
            !EMAIL_PATTERN.matches(trimmed) -> EmailError.INVALID_FORMAT
            else -> null
        }
    }

    fun validatePassword(password: String): PasswordError? {
        return when {
            password.isBlank() -> PasswordError.REQUIRED
            password.length < MIN_PASSWORD_LENGTH -> PasswordError.TOO_SHORT
            else -> null
        }
    }
}
