package com.example.bitfitpart2

object EntryValidator {
    const val MAX_FOOD_NAME_LENGTH = 100
    const val MAX_PROTEIN_AMOUNT = 1000.0

    enum class FoodNameError {
        REQUIRED,
        TOO_LONG
    }

    enum class ProteinAmountError {
        INVALID_NUMBER,
        NOT_POSITIVE,
        TOO_LARGE
    }

    fun validateFoodName(name: String): FoodNameError? {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> FoodNameError.REQUIRED
            trimmed.length > MAX_FOOD_NAME_LENGTH -> FoodNameError.TOO_LONG
            else -> null
        }
    }

    fun validateProteinAmount(amount: Double?): ProteinAmountError? {
        return when {
            amount == null || !amount.isFinite() -> ProteinAmountError.INVALID_NUMBER
            amount <= 0 -> ProteinAmountError.NOT_POSITIVE
            amount > MAX_PROTEIN_AMOUNT -> ProteinAmountError.TOO_LARGE
            else -> null
        }
    }
}
