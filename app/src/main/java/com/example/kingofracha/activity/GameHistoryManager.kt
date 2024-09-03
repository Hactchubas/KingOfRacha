// GameHistoryManager.kt
package com.example.kingofracha.activity

import android.content.Context
import com.example.kingofracha.model.GameResult

// Função para salvar o resultado do jogo
fun saveGameResult(context: Context, points: Int, players: String) {
    val sharedPreferences = context.getSharedPreferences("GameHistory", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Adicionando um novo resultado de jogo
    val gameResult = "$points - $players"
    val currentHistory = sharedPreferences.getString("game_history", "") ?: ""
    val newHistory = "$gameResult\n$currentHistory"

    // Mantendo apenas os 5 últimos resultados
    val historyLines = newHistory.split("\n").filter { it.isNotEmpty() }.take(5)
    editor.putString("game_history", historyLines.joinToString("\n"))
    editor.apply()
}

// Função para obter o histórico dos jogos
fun getGameHistory(context: Context): List<GameResult> {
    val sharedPreferences = context.getSharedPreferences("GameHistory", Context.MODE_PRIVATE)
    val history = sharedPreferences.getString("game_history", "") ?: ""

    // Transformando o histórico salvo em uma lista de GameResult
    return history.split("\n").filter { it.isNotEmpty() }.map { line ->
        val parts = line.split(" - ")
        // Certifique-se de que o GameResult é uma classe que pode lidar com a conversão dos dados
        GameResult(parts[0].toInt(), parts[1])
    }
}
