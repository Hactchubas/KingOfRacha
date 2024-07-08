package com.example.kingofracha.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    var orderedTeams: MutableList<Team>,
    var crown: Team,
) : Parcelable {
    constructor(gameState: GameState) : this(
        mutableListOf<Team>().apply {
            add(Team(gameState.challenger))
            for (team in gameState.orderedTeams) {
                add(Team(team))
            }
        },
        Team(
            arrayListOf<String>().apply {
                addAll(gameState.crown.players)
            },
            gameState.crown.points
        )
    )

    var challenger: Team

    init {
        challenger = orderedTeams.removeFirst()
    }

    override fun toString(): String {
        return "\n$crown x $challenger\n\t${orderedTeams.joinToString("    ,    ")}"
    }

    fun clone(gameState: GameState): GameState {
        return GameState(
            mutableListOf<Team>().apply {
                add(Team(gameState.challenger))
                for (team in gameState.orderedTeams) {
                    add(Team(team))
                }
            },
            Team(
                arrayListOf<String>().apply {
                    addAll(gameState.crown.players)
                },
                gameState.crown.points
            )
        )
    }


}
