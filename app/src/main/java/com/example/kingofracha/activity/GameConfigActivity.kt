package com.example.kingofracha.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kingofracha.R
import com.example.kingofracha.classes.Team

class GameConfigActivity : AppCompatActivity() {
    private lateinit var teamsList: ListView
    private val teams = mutableListOf<Team>(
        Team(arrayListOf("Carla","Kauã"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        teamsList = findViewById(R.id.teamsList)
        // Cria o adaptador da lista
        var playersNames = mutableListOf("Kauã | Carla")
        var adapter = ArrayAdapter(
            this,
            R.layout.teams_list_layout,
            R.id.playersNames,
            playersNames
        )

        teamsList.adapter = adapter
    }
}