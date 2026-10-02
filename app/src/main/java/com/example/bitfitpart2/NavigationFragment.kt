package com.example.bitfitpart2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NavigationFragment : Fragment() {
    private val repository = EntryRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        val totalProteinText: TextView = view.findViewById(R.id.totalProteinText)
        val avgProteinText: TextView = view.findViewById(R.id.avgProteinText)
        val dashboardErrorText: TextView = view.findViewById(R.id.dashboardErrorText)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            totalProteinText.visibility = View.GONE
            avgProteinText.visibility = View.GONE
            dashboardErrorText.text = getString(R.string.error_not_signed_in)
            dashboardErrorText.visibility = View.VISIBLE
            return view
        }

        lifecycleScope.launch {
            repository.observeEntries(uid).collect { result ->
                when (result) {
                    is EntriesResult.Loading -> Unit
                    is EntriesResult.Error -> {
                        totalProteinText.visibility = View.GONE
                        avgProteinText.visibility = View.GONE
                        dashboardErrorText.text = getString(R.string.error_loading_entries)
                        dashboardErrorText.visibility = View.VISIBLE
                    }
                    is EntriesResult.Success -> {
                        dashboardErrorText.visibility = View.GONE
                        totalProteinText.visibility = View.VISIBLE
                        avgProteinText.visibility = View.VISIBLE

                        val totalProtein = result.entries.sumOf { it.proteinAmount ?: 0.0 }
                        val avgProtein = if (result.entries.isNotEmpty()) {
                            totalProtein / result.entries.size
                        } else {
                            0.0
                        }

                        totalProteinText.text = "Total Protein: $totalProtein g"
                        avgProteinText.text = "Average Protein Per Meal: ${String.format("%.2f", avgProtein)} g"
                    }
                }
            }
        }
        return view
    }
}
