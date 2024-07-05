package com.example.kingofracha.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kingofracha.R
import com.example.kingofracha.classes.Team

class OffTeamAdapter(var teamsList: MutableList<Team>, var context: Activity) :
    RecyclerView.Adapter<OffTeamAdapter.OffTeam>() {

    inner class OffTeam(itemView: View): RecyclerView.ViewHolder(itemView){
        var points = itemView.findViewById<TextView>(R.id.points)
        var players = itemView.findViewById<TextView>(R.id.players)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffTeam {
        val offTeamView : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.offteam_adapter_layout, parent, false)

        return OffTeam(offTeamView)
    }

    override fun getItemCount(): Int {
        return teamsList.size
    }

    override fun onBindViewHolder(holder: OffTeam, position: Int) {
        val currentItem = teamsList[position]
        holder.points.text = currentItem.points.toString()
        holder.points.setBackgroundColor(currentItem.color)
        holder.players.text = currentItem.playersString()
    }

}