package com.example.kingofracha.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofracha.R
import com.example.kingofracha.classes.Team

class ConfigTeamAdapter(var teamsList: MutableList<Team>, var context: Activity) :
    RecyclerView.Adapter<ConfigTeamAdapter.ConfigTeam>() {

    inner class ConfigTeam(itemView: View): RecyclerView.ViewHolder(itemView){
        var teamColor = itemView.findViewById<TextView>(R.id.teamColor)
        var players = itemView.findViewById<TextView>(R.id.configPlayers)
        var removeButton = itemView.findViewById<ImageButton>(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigTeam {
        val configTeamView : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.configteam_adapter_layout, parent, false)

        return ConfigTeam(configTeamView)
    }

    override fun getItemCount(): Int {
        return teamsList.size
    }

    override fun onBindViewHolder(holder: ConfigTeam, position: Int) {
        val currentItem = teamsList[position]
        holder.players.text = "${currentItem.playersString()}"
        holder.teamColor.setBackgroundColor(currentItem.color)
        holder.removeButton.setOnClickListener {
            teamsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,itemCount)
        }
    }

}
