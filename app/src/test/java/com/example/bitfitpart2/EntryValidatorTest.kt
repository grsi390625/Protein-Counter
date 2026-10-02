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
}
