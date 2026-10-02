package com.example.bitfitpart2

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object FoodLogReportFormatter {
    private val DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.US)

    fun formatGeneratedAt(generatedAt: LocalDateTime): String {
        return generatedAt.format(DISPLAY_FORMAT)
    }
}
