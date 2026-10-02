package com.example.bitfitpart2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EntryAdapter(
    private val entries: List<DisplayEntry>,
    private val onEntryClick: (DisplayEntry) -> Unit,
    private val onDeleteClick: (DisplayEntry) -> Unit
) : RecyclerView.Adapter<EntryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodNameText: TextView = itemView.findViewById(R.id.FoodNameText)
        val proteinAmountText: TextView = itemView.findViewById(R.id.ProteinAmountText)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.entry_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.foodNameText.text = entry.foodName
        holder.proteinAmountText.text = "Protein: ${entry.proteinAmount} g"
        holder.itemView.setOnClickListener { onEntryClick(entry) }
        holder.deleteButton.setOnClickListener { onDeleteClick(entry) }
    }

    override fun getItemCount() = entries.size
}
