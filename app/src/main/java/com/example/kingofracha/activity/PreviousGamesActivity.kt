package com.example.kingofracha.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofracha.R
import com.example.kingofracha.adapter.HistoryAdapter

class PreviousGamesActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_previous_games)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        val sharedPreferences = getSharedPreferences("GameHistory", Context.MODE_PRIVATE)
        val gameHistory = sharedPreferences.getString("game_history", "No games recorded.")?.split("\n\n") ?: listOf("No games recorded.")

        historyAdapter = HistoryAdapter(gameHistory, this)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }
}
