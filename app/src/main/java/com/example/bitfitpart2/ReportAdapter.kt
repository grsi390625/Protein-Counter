package com.example.bitfitpart2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val entries: List<DisplayEntry>) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodNameText: TextView = itemView.findViewById(R.id.reportFoodNameText)
        val proteinAmountText: TextView = itemView.findViewById(R.id.reportProteinAmountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.foodNameText.text = entry.foodName
        holder.proteinAmountText.text = "${entry.proteinAmount} g"
    }

    override fun getItemCount() = entries.size
}
