package com.example.bitfitpart2

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EntryFragment : Fragment() {
    private val searchQuery = MutableStateFlow("")
    private val repository = EntryRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entry, container, false)
        val searchEntry: EditText = view.findViewById(R.id.searchEntry)
        val recyclerView: RecyclerView = view.findViewById(R.id.entryRV)
        val emptyStateText: TextView = view.findViewById(R.id.emptyStateText)
        val entryList = mutableListOf<DisplayEntry>()
        val adapter = EntryAdapter(
            entries = entryList,
            onEntryClick = { entry -> openEditScreen(entry) },
            onDeleteClick = { entry -> confirmDelete(entry) }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchEntry.addTextChangedListener { text ->
            searchQuery.value = text?.toString() ?: ""
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            recyclerView.visibility = View.GONE
            emptyStateText.text = getString(R.string.error_not_signed_in)
            emptyStateText.visibility = View.VISIBLE
            return view
        }

        lifecycleScope.launch {
            combine(repository.observeEntries(uid), searchQuery) { result, rawQuery ->
                Pair(result, rawQuery)
            }.collect { (result, rawQuery) ->
                when (result) {
                    is EntriesResult.Loading -> Unit
                    is EntriesResult.Error -> {
                        entryList.clear()
                        adapter.notifyDataSetChanged()
                        recyclerView.visibility = View.GONE
                        emptyStateText.text = getString(R.string.error_loading_entries)
                        emptyStateText.visibility = View.VISIBLE
                    }
                    is EntriesResult.Success -> {
                        val normalizedQuery = SearchQueryNormalizer.normalize(rawQuery)
                        val filtered = result.entries.filter {
                            SearchQueryNormalizer.matches(it.foodName, normalizedQuery)
                        }
                        entryList.clear()
                        entryList.addAll(filtered)
                        adapter.notifyDataSetChanged()

                        val hasResults = filtered.isNotEmpty()
                        recyclerView.visibility = if (hasResults) View.VISIBLE else View.GONE
                        emptyStateText.text = getString(R.string.no_matching_entries)
                        emptyStateText.visibility = if (hasResults) View.GONE else View.VISIBLE
                    }
                }
            }
        }
        return view
    }

    private fun openEditScreen(entry: DisplayEntry) {
        val id = entry.id ?: return
        val intent = Intent(activity, AddEntryActivity::class.java).apply {
            putExtra(AddEntryActivity.EXTRA_ENTRY_ID, id)
            putExtra(AddEntryActivity.EXTRA_FOOD_NAME, entry.foodName ?: "")
            putExtra(AddEntryActivity.EXTRA_PROTEIN_AMOUNT, entry.proteinAmount ?: 0.0)
        }
        startActivity(intent)
    }

    private fun confirmDelete(entry: DisplayEntry) {
        val id = entry.id ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_entry_title))
            .setMessage(getString(R.string.delete_entry_message, entry.foodName ?: ""))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                repository.deleteEntry(uid, id).addOnFailureListener { exception ->
                    val context = context ?: return@addOnFailureListener
                    Toast.makeText(
                        context,
                        getString(R.string.error_delete_failed, exception.message ?: ""),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
