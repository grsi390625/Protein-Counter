package com.example.bitfitpart2

import com.google.firebase.Timestamp

data class FoodEntryDocument(
    val foodName: String? = null,
    val proteinAmount: Double? = null,
    val createdAt: Timestamp? = null
)
