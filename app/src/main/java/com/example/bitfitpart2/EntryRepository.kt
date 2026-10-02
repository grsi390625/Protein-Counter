package com.example.bitfitpart2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class EntriesResult {
    object Loading : EntriesResult()
    data class Success(val entries: List<DisplayEntry>) : EntriesResult()
    data class Error(val message: String) : EntriesResult()
}

class EntryRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun entriesCollection(uid: String) =
        firestore.collection("users").document(uid).collection("entries")

    // Drops any callback that fires after the signed-in user changes, so a stale snapshot never reaches a different user's screen.
    fun observeEntries(uid: String): Flow<EntriesResult> = callbackFlow {
        trySend(EntriesResult.Loading)
        val registration = entriesCollection(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (FirebaseAuth.getInstance().currentUser?.uid != uid) {
                    return@addSnapshotListener
                }
                if (error != null) {
                    trySend(EntriesResult.Error(error.message ?: "Unable to load entries."))
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.map { document ->
                    val data = document.toObject(FoodEntryDocument::class.java)
                    DisplayEntry(document.id, data?.foodName, data?.proteinAmount)
                } ?: emptyList()
                trySend(EntriesResult.Success(entries))
            }
        awaitClose { registration.remove() }
    }

    fun addEntry(uid: String, foodName: String, proteinAmount: Double): Task<DocumentReference> {
        val data = hashMapOf<String, Any>(
            "foodName" to foodName,
            "proteinAmount" to proteinAmount,
            "createdAt" to FieldValue.serverTimestamp()
        )
        return entriesCollection(uid).add(data)
    }

    fun updateEntry(uid: String, entryId: String, foodName: String, proteinAmount: Double): Task<Void> {
        val data = mapOf(
            "foodName" to foodName,
            "proteinAmount" to proteinAmount
        )
        return entriesCollection(uid).document(entryId).update(data)
    }

    fun deleteEntry(uid: String, entryId: String): Task<Void> {
        return entriesCollection(uid).document(entryId).delete()
    }
}
