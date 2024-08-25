package com.example.kingofracha.classes

import kotlin.math.max

data class RoundHistory(
    var history: ArrayList<GameState> = arrayListOf(),
    var startTime: Long? = null,
    var endTime: Long? = null,
) {
    val winner: Team?
        get() {
            if (history.size > 0) {
                val endGame = history.last().clone()
                var allTeams = arrayListOf(
                    endGame.crown,
                    endGame.challenger)
                allTeams.addAll(endGame.orderedTeams)
                return allTeams.reduce { a, b ->
                    if (a.points > b.points) a else b
                }
            } else return null
        }
}