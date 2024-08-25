package com.example.kingofracha.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kingofracha.R
import com.example.kingofracha.classes.Team
import com.example.kingofracha.classes.data.GameConfig
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun newGame( view: View){
        val intent = Intent(this, GameConfigActivity::class.java).apply {
            putExtra("newGame", true)
        }
        startActivity(intent)
    }

    fun previousGames( view: View){
        val intent = Intent(this, PreviousGamesActivity::class.java)
        startActivity(intent)
    }

    fun gameConfigs( view: View){
        val intent = Intent(this, GameConfigActivity::class.java).apply {
            putExtra("newGame", false)
        }
        startActivity(intent)
    }

    fun gameRules( view: View){
        val intent = Intent(this, GameRulesActivity::class.java)
        startActivity(intent)
    }

}

