package com.example.bitfitpart2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntryValidatorTest {

    @Test
    fun invalidEntry_blankNameAndNonPositiveProtein_returnsErrors() {
        val foodNameError = EntryValidator.validateFoodName("   ")
        val proteinError = EntryValidator.validateProteinAmount(-5.0)

        assertEquals(EntryValidator.FoodNameError.REQUIRED, foodNameError)
        assertEquals(EntryValidator.ProteinAmountError.NOT_POSITIVE, proteinError)
    }

    @Test
    fun validEntry_realisticNameAndProtein_returnsNoErrors() {
        val foodNameError = EntryValidator.validateFoodName("Chicken breast")
        val proteinError = EntryValidator.validateProteinAmount(30.0)

        assertNull(foodNameError)
        assertNull(proteinError)
    }

    @Test
    fun validateProteinAmount_null_returnsInvalidNumber() {
        assertEquals(EntryValidator.ProteinAmountError.INVALID_NUMBER, EntryValidator.validateProteinAmount(null))
    }

    @Test
    fun validateProteinAmount_zero_returnsNotPositive() {
        assertEquals(EntryValidator.ProteinAmountError.NOT_POSITIVE, EntryValidator.validateProteinAmount(0.0))
    }

    @Test
    fun validateProteinAmount_negative_returnsNotPositive() {
        assertEquals(EntryValidator.ProteinAmountError.NOT_POSITIVE, EntryValidator.validateProteinAmount(-10.0))
    }

    @Test
    fun validateProteinAmount_NaN_returnsInvalidNumber() {
        assertEquals(EntryValidator.ProteinAmountError.INVALID_NUMBER, EntryValidator.validateProteinAmount(Double.NaN))
    }

    @Test
    fun validateProteinAmount_positiveInfinity_returnsInvalidNumber() {
        assertEquals(
            EntryValidator.ProteinAmountError.INVALID_NUMBER,
            EntryValidator.validateProteinAmount(Double.POSITIVE_INFINITY)
        )
    }

    @Test
    fun validateProteinAmount_negativeInfinity_returnsInvalidNumber() {
        assertEquals(
            EntryValidator.ProteinAmountError.INVALID_NUMBER,
            EntryValidator.validateProteinAmount(Double.NEGATIVE_INFINITY)
        )
    }

    @Test
    fun validateProteinAmount_exactlyAtMaxBoundary_returnsNoError() {
        assertNull(EntryValidator.validateProteinAmount(EntryValidator.MAX_PROTEIN_AMOUNT))
    }

    @Test
    fun validateProteinAmount_justAboveMaxBoundary_returnsTooLarge() {
        val justOver = EntryValidator.MAX_PROTEIN_AMOUNT + 0.1
        assertEquals(EntryValidator.ProteinAmountError.TOO_LARGE, EntryValidator.validateProteinAmount(justOver))
    }

    @Test
    fun validateFoodName_exactlyAtMaxLength_returnsNoError() {
        val name = "a".repeat(EntryValidator.MAX_FOOD_NAME_LENGTH)
        assertNull(EntryValidator.validateFoodName(name))
    }

    @Test
    fun validateFoodName_oneCharacterOverMaxLength_returnsTooLong() {
        val name = "a".repeat(EntryValidator.MAX_FOOD_NAME_LENGTH + 1)
        assertEquals(EntryValidator.FoodNameError.TOO_LONG, EntryValidator.validateFoodName(name))
    }
}
