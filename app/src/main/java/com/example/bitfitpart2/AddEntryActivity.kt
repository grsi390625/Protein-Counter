package com.example.bitfitpart2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth

class AddEntryActivity : AppCompatActivity() {
    private lateinit var foodNameEntry: EditText
    private lateinit var proteinAmountEntry: EditText
    private lateinit var saveButton: Button
    private lateinit var saveErrorText: TextView
    private lateinit var saveProgressBar: ProgressBar
    private var editingEntryId: String? = null
    private var isSaving = false
    private val repository = EntryRepository()

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
        const val EXTRA_FOOD_NAME = "extra_food_name"
        const val EXTRA_PROTEIN_AMOUNT = "extra_protein_amount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_entry_activity)

        foodNameEntry = findViewById(R.id.FoodNameEntry)
        proteinAmountEntry = findViewById(R.id.ProteinAmountEntry)
        saveButton = findViewById(R.id.saveButton)
        saveErrorText = findViewById(R.id.saveErrorText)
        saveProgressBar = findViewById(R.id.saveProgressBar)

        val entryId = intent.getStringExtra(EXTRA_ENTRY_ID)
        if (entryId != null) {
            editingEntryId = entryId
            foodNameEntry.setText(intent.getStringExtra(EXTRA_FOOD_NAME))
            proteinAmountEntry.setText(intent.getDoubleExtra(EXTRA_PROTEIN_AMOUNT, 0.0).toString())
            saveButton.text = getString(R.string.save_changes)
        }

        foodNameEntry.addTextChangedListener { checkInputs() }
        proteinAmountEntry.addTextChangedListener { checkInputs() }
        checkInputs()

        saveButton.setOnClickListener { attemptSave() }
    }

    private fun attemptSave() {
        if (isSaving) return

        val foodName = foodNameEntry.text.toString().trim()
        val proteinAmount = proteinAmountEntry.text.toString().toDoubleOrNull()

        if (validateFoodName(foodName) != null || validateProteinAmount(proteinAmount) != null) {
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            showSaveError(getString(R.string.error_not_signed_in))
            return
        }

        isSaving = true
        hideSaveError()
        setSaving(true)

        val entryId = editingEntryId
        val task = if (entryId != null) {
            repository.updateEntry(uid, entryId, foodName, proteinAmount!!)
        } else {
            repository.addEntry(uid, foodName, proteinAmount!!)
        }

        // Firestore's write Task only resolves on server ack, which can hang indefinitely
        // offline; the local cache (and the list screen's pending indicator) already reflects
        // the write immediately, so we close here instead of blocking on that Task.
        val appContext = applicationContext
        task.addOnFailureListener { exception ->
            Toast.makeText(
                appContext,
                appContext.getString(R.string.error_save_failed, exception.message ?: ""),
                Toast.LENGTH_LONG
            ).show()
        }
        finish()
    }

    private fun setSaving(isSaving: Boolean) {
        saveProgressBar.visibility = if (isSaving) View.VISIBLE else View.GONE
        saveButton.isEnabled = !isSaving
        foodNameEntry.isEnabled = !isSaving
        proteinAmountEntry.isEnabled = !isSaving
    }

    private fun showSaveError(message: String) {
        saveErrorText.text = message
        saveErrorText.visibility = View.VISIBLE
    }

    private fun hideSaveError() {
        saveErrorText.text = ""
        saveErrorText.visibility = View.GONE
    }

    private fun foodNameErrorMessage(error: EntryValidator.FoodNameError?): String? = when (error) {
        null -> null
        EntryValidator.FoodNameError.REQUIRED -> getString(R.string.error_food_name_required)
        EntryValidator.FoodNameError.TOO_LONG -> getString(R.string.error_food_name_too_long)
    }

    private fun proteinAmountErrorMessage(error: EntryValidator.ProteinAmountError?): String? = when (error) {
        null -> null
        EntryValidator.ProteinAmountError.INVALID_NUMBER -> getString(R.string.error_protein_invalid_number)
        EntryValidator.ProteinAmountError.NOT_POSITIVE -> getString(R.string.error_protein_not_positive)
        EntryValidator.ProteinAmountError.TOO_LARGE -> getString(R.string.error_protein_too_large)
    }

    private fun validateFoodName(name: String): String? =
        foodNameErrorMessage(EntryValidator.validateFoodName(name))

    private fun validateProteinAmount(amount: Double?): String? =
        proteinAmountErrorMessage(EntryValidator.validateProteinAmount(amount))

    private fun checkInputs() {
        val foodName = foodNameEntry.text.toString().trim()
        val proteinAmount = proteinAmountEntry.text.toString().toDoubleOrNull()

        val foodNameError = if (foodNameEntry.text.isNotEmpty()) validateFoodName(foodName) else null
        val proteinError = if (proteinAmountEntry.text.isNotEmpty()) validateProteinAmount(proteinAmount) else null

        foodNameEntry.error = foodNameError
        proteinAmountEntry.error = proteinError

        saveButton.isEnabled = foodNameEntry.text.isNotBlank() &&
                proteinAmountEntry.text.isNotBlank() &&
                foodNameError == null &&
                proteinError == null
    }
}
