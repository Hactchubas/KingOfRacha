package com.example.kingofracha.activity

import android.os.Bundle
import android.os.Parcelable
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

    private var round = 0
    private var totalrounds = 0
    private var pointsForRoundWin = 0

    private var offTeams : MutableList<Team> = mutableListOf()

    private var offTeamAdapter: OffTeamAdapter? = null
    private lateinit var crown: Team
    private lateinit var challenger: Team
    private var history = mutableListOf<GameState>()
    private lateinit var currentGameState: GameState
    private var gameStateIndex: Int =0

    private lateinit var offCourtTeamsView: RecyclerView
    private lateinit var crownTeamPlayers: TextView
    private lateinit var crownTeamPoints: TextView
    private lateinit var crownTeamField: View
    private lateinit var challTeamPlayers: TextView
    private lateinit var challTeamPoints: TextView
    private lateinit var challTeamField: View
    private lateinit var roundView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (intent.extras != null) {
            val extras = intent.extras
            offTeams = extras!!.getParcelableArrayList<Team>("teams")?.toMutableList()!!
            totalrounds = extras!!.getInt("rounds")
            pointsForRoundWin = extras!!.getInt("points")
            Log.v("K_DEBUG - Rounds", totalrounds.toString())
            Log.v("K_DEBUG - Points", pointsForRoundWin.toString())
            Log.v("K_DEBUG - Teams", offTeams.toString())
        }

        intializeViews()

        crown = offTeams.removeFirst()
        currentGameState = GameState(offTeams, crown)
        challenger = Team(currentGameState.challenger)

        //Update game
        updateState()
        setupAdapter()

    }

    fun setupAdapter(){
        // Config Adpter
        offTeamAdapter = OffTeamAdapter(offTeams, this)
        // Config RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        offCourtTeamsView.setHasFixedSize(true)
        offCourtTeamsView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        offCourtTeamsView.layoutManager = layoutManager
        offCourtTeamsView.adapter = offTeamAdapter
    }

    fun intializeViews(){
        //
        offCourtTeamsView = findViewById(R.id.offCourtTeams)
        roundView = findViewById(R.id.currentRound)
        // Initialize Crown and Challengers
        crownTeamPlayers = findViewById<TextView>(R.id.crownTeamPlayers)
        crownTeamPoints = findViewById<TextView>(R.id.crownTeamPoints)
        crownTeamField = findViewById<View>(R.id.crown)
        challTeamPlayers = findViewById<TextView>(R.id.challTeamPlayers)
        challTeamPoints = findViewById<TextView>(R.id.challTeamPoints)
        challTeamField = findViewById<View>(R.id.challenger)

    }

    fun CrownPoint(view: View) {
        currentGameState.crown.points++

        var last = challenger
        challenger = offTeams.removeFirst()
        offTeamAdapter?.notifyItemRemoved(0)
        offTeams.add(last)
        offTeamAdapter?.notifyItemInserted(offTeams.size - 1)

        updateState()
    }
    fun ChallPoint(view: View) {
        val last = crown
        crown = challenger
        challenger = offTeams.removeFirst()
        offTeamAdapter?.notifyItemRemoved(0)
        offTeams.add(last)
        offTeamAdapter?.notifyItemInserted(offTeams.size - 1)

        updateState()
    }

    fun updateState() {
        currentGameState.orderedTeams = offTeams
        currentGameState.challenger = challenger
        currentGameState.crown = crown

        history.add(GameState(currentGameState))
        // Crowned Team
        crownTeamPlayers.text = currentGameState.crown.playersString()
        crownTeamPoints.text = "${currentGameState.crown.points}"
        crownTeamField.setBackgroundColor(currentGameState.crown.color)
        // Challenger Team
        challTeamPlayers.text = currentGameState.challenger.playersString()
        challTeamPoints.text = "${currentGameState.challenger.points}"
        challTeamField.setBackgroundColor(currentGameState.challenger.color)

    }

}