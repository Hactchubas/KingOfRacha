package com.example.kingofracha.classes

import android.graphics.Color
import android.os.Parcelable

class Team (var players: ArrayList<String>, var points: Int = 0, var color: Int = Color.RED  ){
    constructor(team: Team) : this(team.players, team.points, team.color)
    constructor(players: ArrayList<String>) : this(players, 0, Color.RED)

    override fun toString(): String {

        return "${players.joinToString(", ")} | $points"
    }

    fun playersString(): String{
        var result = ""
        var range = players.size -1
        for (i in 0..<range){
            result += "${players[i]} | "
        }
        result += "${players[range]}"
        return result
    }
}