package com.example.kingofracha.classes.data

import android.os.Parcelable
import com.example.kingofracha.classes.Team
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameConfig(
    var rounds: Int,
    var roundTime: String,
    var roundPoints: Int,
    var teams: ArrayList<Team>,) : Parcelable {
}