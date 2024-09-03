package com.example.kingofracha.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofracha.R

class HistoryAdapter(private val historyList: List<String>, private val context: Context) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameTitleTextView: TextView = itemView.findViewById(R.id.gameTitleTextView)
        val teamsTextView: TextView = itemView.findViewById(R.id.teamsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyEntry = historyList[position]

        // Divide o texto em título e detalhes dos times
        val lines = historyEntry.split("\n")
        val gameTitle = lines.getOrNull(0) ?: ""
        val teamsText = lines.drop(1).joinToString("\n")

        holder.gameTitleTextView.text = gameTitle
        holder.teamsTextView.text = teamsText
    }

    override fun getItemCount(): Int = historyList.size
}
