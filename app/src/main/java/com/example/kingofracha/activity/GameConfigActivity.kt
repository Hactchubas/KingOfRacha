package com.example.kingofracha.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofracha.R
import com.example.kingofracha.adapter.ConfigTeamAdapter
import com.example.kingofracha.classes.Team
import com.example.kingofracha.classes.data.GameConfig
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList
import kotlin.random.Random

class GameConfigActivity : AppCompatActivity() {
    private lateinit var teamsListView: RecyclerView
    private  var configTeamAdapter: ConfigTeamAdapter? = null
    private val configTeamsList = mutableListOf<Team>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        teamsListView = findViewById(R.id.teamsList)
        // Config Adapter
        configTeamAdapter = ConfigTeamAdapter(configTeamsList, this)
        // Config RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        teamsListView.setHasFixedSize(false)
        teamsListView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        teamsListView.layoutManager = layoutManager
        teamsListView.adapter = configTeamAdapter

    }


    fun addTeam(view: View){
        var captain =  findViewById<TextView>(R.id.player1Text)
        var partner =  findViewById<TextView>(R.id.player2Text)
        Log.v("K_DEBUG - PLayers", "${captain.text.split(" ")} and ${partner.text.split(" ")}")
        Log.v("K_DEBUG - PLayers", "${captain.text.split(" ").size} and ${partner.text.split(" ").size}")
        if (captain.text.isNotEmpty() && partner.text.isNotEmpty()) {
            var newPlayers = arrayListOf<String>(
                captain.text.toString(),
                partner.text.toString()
            )
            val newTeam = Team(
                newPlayers,
                0,
                Color.argb(
                    255,
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256),
                )
            )

            configTeamsList.add(newTeam)
            configTeamAdapter?.notifyItemInserted(configTeamsList.size - 1)

            captain.text = ""
            partner.text = ""
        } else{
            Toast.makeText(
                this,
                "Please inform both players names",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun createGame(view: View){
        val roundsText = findViewById<TextView>(R.id.roundsEditView).text.toString()
        val rounds : Int? = if (roundsText == "") null else Integer.valueOf(roundsText)

        val roundTime = findViewById<TextView>(R.id.roundTimeEditView).text.toString()

        val pointsText = findViewById<TextView>(R.id.roundPointsEditView).text.toString()
        val points : Int? = if (pointsText == "") null else Integer.valueOf(pointsText)

        if (
            configTeamsList.size > 2
            && (rounds != null && rounds >= 1)
            && (points != null || (roundTime != "" && roundTime.split(":").size > 2))
            ){
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("config", GameConfig(rounds, roundTime, points, configTeamsList as ArrayList<Team>))
            }
            startActivity(intent)
        } else{
            Toast.makeText(
                this,
                when {
                    configTeamsList.size <= 2 -> "There must be at leat 3 teams"
                    (rounds == null || rounds < 1) -> "Enter how many rounds will be played (min: 1)"
                    (points == null && roundTime == "") -> "Enter the points needed to win the round and/or the round time"
                    else -> "Enter a valid time format (Min:Sec)"
                },
                Toast.LENGTH_LONG
            ).show()
        }
    }
}