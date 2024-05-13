package com.nasupe.gamequest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SavedGamesActivity : AppCompatActivity(), GamesAdapter.OnGameSavedChangedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var savedGames: MutableList<Game>
    private lateinit var btnBack2: ImageView
    private lateinit var noSavedGamesMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_games)

        recyclerView = findViewById(R.id.saved_games_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar el mensaje de "No hay juegos guardados"
        noSavedGamesMessage = findViewById(R.id.no_saved_games_message)

        // Configurar el botón de retroceso
        btnBack2 = findViewById(R.id.btnBack2)
        btnBack2.setOnClickListener {
            onBackPressed()
        }

        // Obtener los juegos guardados desde el administrador de juegos guardados
        val managerSavedGames = ManagerSavedGames(this)
        savedGames = managerSavedGames.getSavedGames().filter { it.isSaved }.toMutableList()

        // Mostrar el RecyclerView si hay juegos guardados, o mostrar el mensaje si no hay juegos guardados
        if (savedGames.isEmpty()) {
            recyclerView.visibility = View.GONE
            noSavedGamesMessage.visibility = View.VISIBLE
            Log.d("SavedGames", " SUPUESTO VACIO 2Contenido del JSON: $savedGames")

        } else {
            recyclerView.visibility = View.VISIBLE
            noSavedGamesMessage.visibility = View.GONE

            // Configurar y mostrar los juegos guardados en el RecyclerView
            val adapter = GamesAdapter(savedGames, this)
            recyclerView.adapter = adapter
        }
    }

    // Implementación de la interfaz para manejar los cambios en el estado de guardado del juego
    override fun onGameSavedChanged(game: Game) {
        val index = savedGames.indexOfFirst { it.nombre == game.nombre }

        if (index != -1) {
            savedGames[index] = game
            val managerSavedGames = ManagerSavedGames(this)
            if (game.isSaved) {
                managerSavedGames.saveGame(game)
            } else {
                savedGames.removeAt(index)
                managerSavedGames.saveGamesToFile(savedGames) // Guardar los juegos actualizados en el archivo JSON
                Toast.makeText(
                    this,
                    "El juego ${game.nombre} se ha eliminado de los juegos guardados.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            }
        // Actualizar la visibilidad del mensaje 'No hay juegos guardados' después de actualizar la lista
        updateNoSavedGamesMessageVisibility()
        recyclerView.adapter?.notifyItemRemoved(index)

        // Verificar si la lista de juegos guardados está vacía
        if (savedGames.isEmpty()) {
            // Si la lista está vacía, borrar el archivo JSON
            val managerSavedGames = ManagerSavedGames(this)
            managerSavedGames.clearSavedGames() // Agrega este método en tu clase ManagerSavedGames para borrar el archivo JSON
        }
    }



    private fun updateNoSavedGamesMessageVisibility() {
        if (savedGames.isEmpty()) {
            recyclerView.visibility = View.GONE
            noSavedGamesMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noSavedGamesMessage.visibility = View.GONE
        }
    }
}


