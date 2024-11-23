package com.example.bitfitpart2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry_table")
data class EntryEntity(
    @ColumnInfo(name = "food_name") val foodName: String?,
    @ColumnInfo(name = "protein_amount") val proteinAmount: Double?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)