package com.example.kingofracha.activity

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
import com.example.kingofracha.adapter.OffTeamAdapter
import com.example.kingofracha.classes.GameState
import com.example.kingofracha.classes.Team

class GameActivity : AppCompatActivity() {

    private var offTeams = mutableListOf(
        Team(arrayListOf("Kauã", "Carla")),
        Team(arrayListOf("Matheus", "Feyman")),
        Team(arrayListOf("Eduardo", "Thiago")),
        Team(arrayListOf("Caíque", "Mary")),
        Team(arrayListOf("Arlen", "Iury")),
    )

    private var offTeamAdapter: OffTeamAdapter? = null
    private lateinit var crown: Team
    private lateinit var challenger: Team
    private var history = mutableListOf<GameState>()
    private lateinit var currentGameState: GameState
    private var gameStateIndex: Int =0

    private lateinit var offCourtTeamsView: RecyclerView
    private lateinit var crownTeamPlayers: TextView
    private lateinit var crownTeamPoints: TextView
    private lateinit var challTeamPlayers: TextView
    private lateinit var challTeamPoints: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initialize Crown and Challengers
        crownTeamPlayers = findViewById<TextView>(R.id.crownTeamPlayers)
        crownTeamPoints = findViewById<TextView>(R.id.crownTeamPoints)
        challTeamPlayers = findViewById<TextView>(R.id.challTeamPlayers)
        challTeamPoints = findViewById<TextView>(R.id.challTeamPoints)


        crown = offTeams.removeFirst()

        currentGameState = GameState(offTeams, crown)

        //Update game
        updateState(currentGameState)

        offCourtTeamsView = findViewById(R.id.offCourtTeams)
        // Config Adpter
        offTeamAdapter = OffTeamAdapter(offTeams, this)
        // Config RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        offCourtTeamsView.setHasFixedSize(true)
        offCourtTeamsView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        offCourtTeamsView.layoutManager = layoutManager
        offCourtTeamsView.adapter = offTeamAdapter

    }

    fun CrownPoint(view: View) {
        var newGameState = GameState(currentGameState)
        newGameState.crown.points++
        val last = newGameState.orderedTeams.removeFirst()
        newGameState.orderedTeams.add(last)
        updateState(newGameState)
    }
    fun ChallPoint(view: View) {
        var newGameState = GameState(currentGameState)
        val chall = newGameState.orderedTeams.removeFirst()
        newGameState.orderedTeams.add(newGameState.crown)
        newGameState.crown = chall
        updateState(newGameState)
    }

    fun updateState(newGameState: GameState) {
        currentGameState = newGameState

        // Crowned Team
        crownTeamPlayers.text = currentGameState.crown.playersString()
        crownTeamPoints.text = "${currentGameState.crown.points}"
        // Challenger Team
        challTeamPlayers.text = currentGameState.orderedTeams.first().playersString()
        challTeamPoints.text = "${currentGameState.orderedTeams.first().points}"


        offTeams = currentGameState.orderedTeams
        offTeamAdapter?.notifyDataSetChanged()

        history.add(GameState(newGameState.orderedTeams, newGameState.crown))
        Log.v("MYHISTORY", history.toString())
    }

}