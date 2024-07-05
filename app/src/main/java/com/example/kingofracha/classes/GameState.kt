package com.example.kingofracha.classes

data class GameState(
    var orderedTeams: MutableList<Team>,
    var crown: Team
) {
    constructor(gameState: GameState): this(
        mutableListOf<Team>().apply {
            addAll(gameState.orderedTeams)
        },
        Team(
            arrayListOf<String>().apply {
                addAll(gameState.crown.players)
            } ,
            gameState.crown.points
        )
    ) {

    }

    override fun toString(): String {
        return "\n${crown} > ${orderedTeams}"
    }
}
