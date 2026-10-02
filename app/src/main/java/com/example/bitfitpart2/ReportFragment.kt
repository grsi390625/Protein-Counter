package com.example.bitfitpart2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ReportFragment : Fragment() {
    private val repository = EntryRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generatedAtText: TextView = view.findViewById(R.id.reportGeneratedAtText)
        val recyclerView: RecyclerView = view.findViewById(R.id.reportRV)
        val emptyStateText: TextView = view.findViewById(R.id.reportEmptyStateText)
        val entryList = mutableListOf<DisplayEntry>()
        val adapter = ReportAdapter(entryList)

        generatedAtText.text = getString(
            R.string.report_generated_label,
            FoodLogReportFormatter.formatGeneratedAt(LocalDateTime.now())
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            recyclerView.visibility = View.GONE
            emptyStateText.text = getString(R.string.error_not_signed_in)
            emptyStateText.visibility = View.VISIBLE
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeEntries(uid).collect { result ->
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
                            entryList.clear()
                            entryList.addAll(result.entries)
                            adapter.notifyDataSetChanged()

                            val hasResults = result.entries.isNotEmpty()
                            recyclerView.visibility = if (hasResults) View.VISIBLE else View.GONE
                            emptyStateText.text = getString(R.string.no_entries_for_report)
                            emptyStateText.visibility = if (hasResults) View.GONE else View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
