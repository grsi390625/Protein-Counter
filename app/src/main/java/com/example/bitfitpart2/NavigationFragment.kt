package com.example.bitfitpart2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class NavigationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        val totalProteinText: TextView = view.findViewById(R.id.totalProteinText)
        val avgProteinText: TextView = view.findViewById(R.id.avgProteinText)

        lifecycleScope.launch {
            (activity?.application as BitFitPart2Application).db.entryDao().getAll().collect { databaseList ->
                val totalProtein = databaseList.sumOf { it.proteinAmount ?: 0.0 }
                val avgProtein = if (databaseList.isNotEmpty()) totalProtein / databaseList.size else 0.0

                totalProteinText.text = "Total Protein: $totalProtein g"
                avgProteinText.text = "Average Protein Per Meal: ${String.format("%.2f", avgProtein)} g"
            }
        }
        return view
    }
}
