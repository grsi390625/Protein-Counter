package com.example.bitfitpart2

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class FoodLogReportFormatterTest {

    @Test
    fun formatGeneratedAt_formatsDateAndTimeConsistently() {
        val fixed = LocalDateTime.of(2026, 9, 27, 14, 5)

        val result = FoodLogReportFormatter.formatGeneratedAt(fixed)

        assertEquals("Sep 27, 2026 2:05 PM", result)
    }
}
