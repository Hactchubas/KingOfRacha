package com.example.kingofracha.classes

import java.sql.Time

class Game (var teams: MutableList<Team>, var rounds: Int, var roundTime: Time, var history: ArrayList<GameState>, var victoryPoints: Int){

}