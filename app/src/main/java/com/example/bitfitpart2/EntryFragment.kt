package com.example.bitfitpart2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class EntryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entry, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.entryRV)
        val entryList = mutableListOf<DisplayEntry>()
        val adapter = EntryAdapter(entryList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            (activity?.application as BitFitPart2Application).db.entryDao().getAll().collect { databaseList ->
                val mappedList = databaseList.map {
                    DisplayEntry(it.id, it.foodName, it.proteinAmount)
                }
                entryList.clear()
                entryList.addAll(mappedList)
                adapter.notifyDataSetChanged()
            }
        }
        return view
    }
}
