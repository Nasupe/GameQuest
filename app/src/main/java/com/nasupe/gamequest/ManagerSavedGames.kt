package com.nasupe.gamequest

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ManagerSavedGames(private val context: Context) {
    private val gson = Gson()
    private val savedGamesFileName = "saved_games.json"

    fun saveGame(game: Game) {
        val savedGames = getSavedGames().toMutableList()
        savedGames.add(game)
        Log.d("ManagerSavedGames", "Juego guardado: $game")
        saveGamesToFile(savedGames)
    }

    fun unsaveGame(game: Game) {
        val savedGames = getSavedGames().toMutableList()
        savedGames.removeAll { it.nombre == game.nombre }
        Log.d("ManagerSavedGames", "MANAGER Juego eliminado: $game")
        Log.d("ManagerSavedGames", "JSON después de eliminar el juego: ${gson.toJson(savedGames)}")
        saveGamesToFile(savedGames)
    }

    fun getSavedGames(): List<Game> {
        val savedGamesFile = File(context.filesDir, savedGamesFileName)
        if (!savedGamesFile.exists()) {
            Log.d("ManagerSavedGames", "No hay juegos guardados")
            return emptyList()
        }
        val jsonString = savedGamesFile.readText()
        Log.d("ManagerSavedGames", "JSON leído: $jsonString")
        val type = object : TypeToken<List<Game>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun saveGamesToFile(savedGames: List<Game>) {
        val savedGamesFile = File(context.filesDir, savedGamesFileName)
        if (savedGames.isEmpty()) {
            // Si no hay juegos guardados, crear un archivo JSON vacío
            savedGamesFile.createNewFile()
        } else {
            // Si hay juegos guardados, guardarlos en el archivo JSON
            val jsonString = gson.toJson(savedGames)
            savedGamesFile.writeText(jsonString)
            Log.d("ManagerSavedGames", "Juegos guardados en el archivo: $jsonString")


        }
    }

    fun clearSavedGames() {
        val file = File(context.filesDir, savedGamesFileName)
        if (file.exists()) {
            file.delete()
        }
    }
}