package com.example.kingofracha.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        teamsListView.setHasFixedSize(false)
        teamsListView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        teamsListView.layoutManager = layoutManager
        teamsListView.adapter = configTeamAdapter

    }


    fun addTeam(view: View){
        var captain =  findViewById<TextView>(R.id.player1Text)
        var partner =  findViewById<TextView>(R.id.player2Text)

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
    }

    fun createGame(view: View){
        val roundsText = findViewById<TextView>(R.id.roundsEditView).text.toString()
        val rounds : Int? = if (roundsText == "") null else Integer.valueOf(roundsText)

        val roundTime = findViewById<TextView>(R.id.roundTimeEditView).text.toString()

        val pointsText = findViewById<TextView>(R.id.roundPointsEditView).text.toString()
        val points : Int? = if (pointsText == "") null else Integer.valueOf(pointsText)

        Log.v("K_DEBUG - Config","Pontos Text: $pointsText, Rounds: $roundsText")
        Log.v("K_DEBUG - Config","Pontos: $points, Rounds: $rounds")
        if (configTeamsList.size > 2 && rounds != null  && points != null ){
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("config", GameConfig(rounds, roundTime, points, configTeamsList as ArrayList<Team>))
            }
            startActivity(intent)
        }
    }
}