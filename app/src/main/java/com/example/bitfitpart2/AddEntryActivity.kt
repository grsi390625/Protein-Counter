package com.example.bitfitpart2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener

class AddEntryActivity : AppCompatActivity() {
    private lateinit var foodNameEntry: EditText
    private lateinit var proteinAmountEntry: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_entry_activity)

        foodNameEntry = findViewById(R.id.FoodNameEntry)
        proteinAmountEntry = findViewById(R.id.ProteinAmountEntry)
        saveButton = findViewById(R.id.saveButton)

        foodNameEntry.addTextChangedListener { checkInputs() }
        proteinAmountEntry.addTextChangedListener { checkInputs() }

        saveButton.setOnClickListener {
            val foodName = foodNameEntry.text.toString()
            val proteinAmount = proteinAmountEntry.text.toString().toDoubleOrNull()

            if (foodName.isNotBlank() && proteinAmount != null) {
                val newEntry = EntryEntity(foodName, proteinAmount)
                CoroutineScope(Dispatchers.IO).launch {
                    (application as BitFitPart2Application).db.entryDao().insert(newEntry)
                }
                finish()
            }
        }
    }

    private fun checkInputs() {
        saveButton.isEnabled =
            foodNameEntry.text.isNotBlank() && proteinAmountEntry.text.isNotBlank()
    }
}
