package com.example.kingofracha.activity

import android.content.Context
import android.content.SharedPreferences
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

    private val ROUND_PAUSED = 4
    private val ROUND_END = 3
    private val ROUND_RUNNING = 2
    private val ROUND_WAITING = 1
    private val GAME_END = 0
    private var roundState = ROUND_WAITING

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

    private lateinit var offCourtTeamsView: RecyclerView
    private lateinit var crownTeamPlayers: TextView
    private lateinit var crownTeamPoints: TextView
    private lateinit var crownTeamField: View
    private lateinit var challTeamPlayers: TextView
    private lateinit var challTeamPoints: TextView
    private lateinit var challTeamField: View
    private lateinit var roundView: TextView

    private val PREFS_NAME = "game_preferences"
    private val KEY_GAME_HISTORY = "game_history"
    private val KEY_WINNER_TEAM = "winner_team"

    private lateinit var timerTextView: TextView
    private var roundTime: Long = 0
    private var startTime: Long = 0
    private var remainingTime: Long = 0
    private var timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val millis: Long = remainingTime - (currentTime - startTime)
            if (millis > 0 && roundState != ROUND_END) {
                timerTextView.text = convertMillisToString(millis)
                timerHandler.postDelayed(this, 500)
            } else if (roundState != ROUND_WAITING) {
                remainingTime = 0
                timerHandler.removeCallbacks(this)
            }
        }
    }

    private var gameEndHandler = Handler(Looper.getMainLooper())
    private var gameEndRunnable = object : Runnable {
        override fun run() {
            if (pointsForRoundWin != null && crown.points >= pointsForRoundWin!!) {
                roundState = ROUND_END
            } else if (remainingTime <= 0) {
                roundState = ROUND_END
            }

            if (roundState == ROUND_END) endRoundWatcher() else gameEndHandler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

        intializeViews()
        setupTimer()

        crown = offTeams.removeFirst()
        currentGameState = GameState(offTeams, crown)
        challenger = Team(currentGameState.challenger)

        updateState()
        setupAdapter()
        gameEndHandler.postDelayed(gameEndRunnable, 500)

        Log.v("K_DEBUG", roundState.toString())
    }

    override fun onPause() {
        super.onPause()
        timerHandler.removeCallbacks(timerRunnable)
        gameEndHandler.removeCallbacks(gameEndRunnable)
    }

    private fun saveGameResult(context: Context) {
        val sharedPreferences = context.getSharedPreferences("GameHistory", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Cria uma entrada para o jogo atual
        val gameResult = StringBuilder()
            .append("Game ${sharedPreferences.getInt("game_count", 0) + 1}\n")
            .append("Teams:\n")
            .append((offTeams + listOf(crown, challenger))
                .sortedByDescending { it.points }
                .joinToString("\n") { "${it.playersString()} : ${it.points} pts" })

        // Atualiza o histórico de jogos
        val previousHistory = sharedPreferences.getString("game_history", "")
        val newHistory = if (previousHistory.isNullOrEmpty()) {
            gameResult.toString()
        } else {
            "$previousHistory\n\n$gameResult"
        }
        editor.putString("game_history", newHistory)

        // Atualiza o contador de jogos
        editor.putInt("game_count", sharedPreferences.getInt("game_count", 0) + 1)
        editor.apply()

        // Adicione um log para verificar se o resultado foi salvo
        Log.d("GameActivity", "Game result saved: $gameResult")
    }


    private fun findWinningTeam(): Team {
        return offTeams.maxByOrNull { it.points } ?: crown
    }

    private fun endRoundWatcher() {
        if (round > totalrounds || (pointsForRoundWin != null && crown.points >= pointsForRoundWin!!)) {
            roundState = GAME_END
            timerTextView.text = "Game End"
            timerTextView.setOnClickListener(null)

            // Salvar resultado do jogo
            saveGameResult(this)

            Toast.makeText(this, "Game result saved!", Toast.LENGTH_SHORT).show()

            // Parar o relógio
            timerHandler.removeCallbacks(timerRunnable)
            timerTextView.setOnClickListener(null)

        } else if (roundState != ROUND_END) {
            gameEndHandler.postDelayed(gameEndRunnable, 500)
        } else {
            timerTextView.text = "Next Round"
            round++
            gameHistory[round - 2].endTime = System.currentTimeMillis()
        }
    }

    fun convertStringToMillis(timeString: String): Long {
        val minSec = timeString.split(":").map { it.toLong() }
        return minSec[0] * 60000 + minSec[1] * 1000
    }

    fun convertMillisToString(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        return String.format("%d:%02d", minutes, seconds % 60)
    }

    fun setupTimer() {
        timerTextView.setOnClickListener {
            if (roundState == GAME_END) return@setOnClickListener

            if (round > gameHistory.size) {
                gameHistory[round - 2].endTime = System.currentTimeMillis()
            }
            if (roundState != ROUND_END) {
                if (!timerHandler.hasCallbacks(timerRunnable)) {
                    roundState = ROUND_RUNNING
                    startTime = System.currentTimeMillis()
                    timerHandler.postDelayed(timerRunnable, 0)
                } else {
                    var currentTime = System.currentTimeMillis()
                    remainingTime -= (currentTime - startTime)
                    timerHandler.removeCallbacks(timerRunnable)
                    timerTextView.text = "(Paused) ${timerTextView.text}"
                    roundState = ROUND_PAUSED
                }
            } else if (round <= totalrounds) {
                setupNewRound()
            }
        }
    }

    private fun setupNewRound() {
        timerTextView.text = "Start Round"
        roundView.text = "Round $round"
        remainingTime = roundTime
        roundState = ROUND_WAITING
        offTeams.apply {
            add(currentGameState.crown)
            add(currentGameState.challenger)
            sortByDescending { it.points }
        }.forEach { it.points = 0 }
        crown = offTeams.removeFirst()
        challenger = offTeams.removeFirst()

        if (offTeams.size > 1) offTeams.removeLast()

        gameHistory.add(RoundHistory())

        gameEndHandler.postDelayed(gameEndRunnable, 500)
        offTeamAdapter?.notifyDataSetChanged()
        updateState()
    }

    fun setupAdapter() {
        offTeamAdapter = OffTeamAdapter(offTeams, this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        offCourtTeamsView.setHasFixedSize(true)
        offCourtTeamsView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        offCourtTeamsView.layoutManager = layoutManager
        offCourtTeamsView.adapter = offTeamAdapter
    }

    fun intializeViews() {
        offCourtTeamsView = findViewById(R.id.offCourtTeams)
        roundView = findViewById(R.id.currentRound)
        crownTeamPlayers = findViewById(R.id.crownTeamPlayers)
        crownTeamPoints = findViewById(R.id.crownTeamPoints)
        crownTeamField = findViewById(R.id.crown)
        challTeamPlayers = findViewById(R.id.challTeamPlayers)
        challTeamPoints = findViewById(R.id.challTeamPoints)
        challTeamField = findViewById(R.id.challenger)
        timerTextView = findViewById(R.id.time)
    }

    fun CrownPoint(view: View) {
        if (roundState != ROUND_RUNNING) return

        currentGameState.crown.points++

        val last = challenger
        challenger = offTeams.removeFirst()
        offTeamAdapter?.notifyItemRemoved(0)
        offTeams.add(last)
        offTeamAdapter?.notifyItemInserted(offTeams.size - 1)

        updateState()
    }

    fun ChallPoint(view: View) {
        if (roundState != ROUND_RUNNING) return

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

        crownTeamPlayers.text = currentGameState.crown.playersString()
        crownTeamPoints.text = "${currentGameState.crown.points}"
        crownTeamField.setBackgroundColor(currentGameState.crown.color)

        challTeamPlayers.text = currentGameState.challenger.playersString()
        challTeamPoints.text = "${currentGameState.challenger.points}"
        challTeamField.setBackgroundColor(currentGameState.challenger.color)

        Log.v("K_DEBUG", gameHistory[round - 1].history.toString())
    }

    fun ctrlZ(view: View) {
        if (gameHistory[round - 1].history.size <= 1) return

        gameHistory[round - 1].history.removeLast()
        val lastState = gameHistory[round - 1].history.removeLast()

        offTeams.apply {
            clear()
            addAll(lastState.orderedTeams)
        }
        challenger = lastState.challenger
        crown = lastState.crown

        offTeamAdapter?.notifyDataSetChanged()
        updateState()
    }
}
