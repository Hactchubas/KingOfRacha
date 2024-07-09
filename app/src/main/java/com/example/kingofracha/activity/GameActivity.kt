package com.example.kingofracha.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.kingofracha.adapter.OffTeamAdapter
import com.example.kingofracha.classes.GameState
import com.example.kingofracha.classes.RoundHistory
import com.example.kingofracha.classes.Team
import com.example.kingofracha.classes.data.GameConfig
import kotlin.math.max

class GameActivity : AppCompatActivity() {

    private var endRound = false
    private lateinit var gameConfig: GameConfig
    private var round = 1
    private var totalrounds = 0
    private var pointsForRoundWin: Int? = null

    private var offTeams: MutableList<Team> = mutableListOf(
        Team(arrayListOf("Kauã", "Carla")),
        Team(arrayListOf("Matheus", "Feyman")),
        Team(arrayListOf("Eduardo", "Thiago")),
        Team(arrayListOf("Caíque", "Mary")),
        Team(arrayListOf("Arlen", "Iury")),
    )

    private var offTeamAdapter: OffTeamAdapter? = null
    private lateinit var crown: Team
    private lateinit var challenger: Team
    private var gameHistory = mutableListOf(RoundHistory())
    private lateinit var currentGameState: GameState
    private var gameStateIndex: Int = 0

    private lateinit var offCourtTeamsView: RecyclerView
    private lateinit var crownTeamPlayers: TextView
    private lateinit var crownTeamPoints: TextView
    private lateinit var crownTeamField: View
    private lateinit var challTeamPlayers: TextView
    private lateinit var challTeamPoints: TextView
    private lateinit var challTeamField: View
    private lateinit var roundView: TextView

    private lateinit var timerTextView: TextView
    private var roundTime: Long = 0
    private var startTime: Long = 0
    private var remainingTime: Long = 0
    private var timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable = object : Runnable {
        override fun run() {
            var currentTime = System.currentTimeMillis()
            val millis: Long = remainingTime - (currentTime - startTime)
            if (millis > 0) {
                timerTextView.text = convertMillisToString(millis)
                timerHandler.postDelayed(this, 500)
            } else {
                remainingTime = 0
                timerTextView.text = "0:00"
                timerHandler.removeCallbacks(this)
            }
        }
    }

    private var gameEndHandler = Handler(Looper.getMainLooper())
    private var gameEndRunnable = object : Runnable {
        override fun run() {
            endRound = when {
                pointsForRoundWin != null && crown.points >= pointsForRoundWin!! -> true
                remainingTime <= 0 -> true
                else -> false
            }
            if (endRound) endRoundWacther() else gameEndHandler.postDelayed(this, 500)
        }
    }


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
            gameConfig = extras?.getParcelable("config")!!
            offTeams = gameConfig.teams
            totalrounds = gameConfig.rounds
            roundTime = convertStringToMillis(gameConfig.roundTime)
            remainingTime = roundTime
            pointsForRoundWin = gameConfig.roundPoints
        }
        roundTime = convertStringToMillis("00:05")
        totalrounds = 3
        pointsForRoundWin = 55
        remainingTime = roundTime

        intializeViews()
        setupTimer()

        crown = offTeams.removeFirst()
        currentGameState = GameState(offTeams, crown)
        challenger = Team(currentGameState.challenger)

        //Update game
        updateState()
        setupAdapter()
        endRoundWacther()
    }

    override fun onPause() {
        super.onPause()
        timerHandler.removeCallbacks(timerRunnable)
        gameEndHandler.removeCallbacks(gameEndRunnable)
    }

    fun endRoundWacther() {//
        if (!endRound) {
            gameEndHandler.postDelayed(gameEndRunnable, 500)
        } else if (round >= totalrounds) {
            timerTextView.text = "Game End"
            Log.v("K_DEBUG", gameHistory.toString())
        } else {
            timerTextView.text = "Next Round"
            round++
            gameHistory[round - 2].endTime = System.currentTimeMillis()
        }

    }

    fun convertStringToMillis(timeString: String): Long {
        val minSec = timeString.split(":").map {
            it.toLong()
        }
        return minSec[0] * 60000 + minSec[1] * 1000
    }

    fun convertMillisToString(millis: Long): String {
        var seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        seconds %= 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun setupTimer() {
        timerTextView.setOnClickListener {
            if (round > gameHistory.size) {
                gameHistory[round - 2].endTime = System.currentTimeMillis()
            }
            if (!endRound) {
                if (!timerHandler.hasCallbacks(timerRunnable)) {
                    startTime = System.currentTimeMillis()
                    timerHandler.postDelayed(timerRunnable, 0)
                } else {
                    var currentTime = System.currentTimeMillis()
                    remainingTime -= (currentTime - startTime)
                    timerHandler.removeCallbacks(timerRunnable)
                    timerTextView.text = "(Paused) ${timerTextView.text}"
                }
            } else if (round <= totalrounds) {
                setupNewRound()
            }
        }
    }

    private fun setupNewRound() {
        timerTextView.text = "Start Round"
        roundView.text = "Round ${(round)}"
        remainingTime = roundTime
        endRound = false

        offTeams.apply {
            add(currentGameState.crown)
            add(currentGameState.challenger)
            sortByDescending {
                it.points
            }
        }
        crown = offTeams.removeFirst()
        challenger = offTeams.removeFirst()
        gameHistory.add(RoundHistory())

        gameEndHandler.postDelayed(gameEndRunnable, 500)
        offTeamAdapter?.notifyDataSetChanged()
        updateState()
    }

    fun setupAdapter() {
        // Config Adpter
        offTeamAdapter = OffTeamAdapter(offTeams, this)

        // Config RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        offCourtTeamsView.setHasFixedSize(true)
        offCourtTeamsView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        offCourtTeamsView.layoutManager = layoutManager
        offCourtTeamsView.adapter = offTeamAdapter
    }

    fun intializeViews() {
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

        // Initialize Timer TextView
        timerTextView = findViewById(R.id.time)

    }

    fun CrownPoint(view: View) {
        if (endRound) return

        currentGameState.crown.points++

        var last = challenger
        challenger = offTeams.removeFirst()
        offTeamAdapter?.notifyItemRemoved(0)
        offTeams.add(last)
        offTeamAdapter?.notifyItemInserted(offTeams.size - 1)

        updateState()
    }

    fun ChallPoint(view: View) {
        if (endRound) return

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

        gameHistory[round - 1].history.add(GameState(currentGameState))
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