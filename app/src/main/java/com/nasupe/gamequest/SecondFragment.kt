package com.nasupe.gamequest

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class SecondFragment : Fragment(), GamesAdapter.OnGameSavedChangedListener {

    private lateinit var games: List<Game>
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var textViewGames: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view)
        searchInput = view.findViewById(R.id.search_input)
        searchButton = view.findViewById(R.id.search_button)
        textViewGames = view.findViewById(R.id.textView2)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar los juegos desde el JSON
        val jsonString = loadGamesJson(requireContext().resources)
        games = loadGamesFromJson(jsonString)

        // Configurar el RecyclerView para mostrar todos los juegos
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = GamesAdapter(games, this) // Pasar el listener al adaptador
        recyclerView.adapter = adapter

        // Configurar el botón de búsqueda
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim().toLowerCase(Locale.getDefault())

            if (query.isEmpty()) {
                // Mostrar un diálogo indicando que se deben rellenar los campos de búsqueda
                showEmptyFieldsDialog()
            } else {
                val filteredGames = filterGames(query)

                if (filteredGames.isEmpty()) {
                    // Mostrar un diálogo indicando que no se encontraron resultados
                    showNoResultsDialog()
                } else {
                    // Actualizar el RecyclerView de búsqueda con los juegos filtrados
                    recyclerView.adapter = GamesAdapter(filteredGames, this)
                }
            }
        }
    }

    // Función para mostrar un diálogo cuando los campos de búsqueda están vacíos
    private fun showEmptyFieldsDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Campos vacíos")
        alertDialogBuilder.setMessage("Por favor, rellena los campos de búsqueda antes de realizar la búsqueda.")
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Función para mostrar un diálogo cuando no se encuentran resultados de búsqueda
    private fun showNoResultsDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("No se encontraron resultados")
        alertDialogBuilder.setMessage("No se encontraron juegos que coincidan con la búsqueda.")
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Función para cargar los juegos desde el JSON
    private fun loadGamesFromJson(jsonString: String): List<Game> {
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("juegos")
        val games: MutableList<Game> = mutableListOf()

        for (i in 0 until jsonArray.length()) {
            val gameJson = jsonArray.getJSONObject(i).toString()
            val game = Gson().fromJson(gameJson, Game::class.java)
            games.add(game)
        }

        return games
    }

    // Función para cargar el JSON de los juegos desde el archivo raw
    private fun loadGamesJson(resources: Resources): String {
        val inputStream = resources.openRawResource(R.raw.juegos)
        val bufferedReader = inputStream.bufferedReader()
        return bufferedReader.use { it.readText() }
    }

    // Función para filtrar los juegos según el criterio de búsqueda
    private fun filterGames(query: String): List<Game> {
        val lowercaseQuery = query.toLowerCase(Locale.getDefault())
        return games.filter { game ->
            game.nombre.toLowerCase(Locale.getDefault()).contains(lowercaseQuery) ||
                    game.materiales.toLowerCase(Locale.getDefault()).contains(lowercaseQuery) ||
                    game.categoria.toLowerCase(Locale.getDefault()).contains(lowercaseQuery) ||
                    game.descripcion.toLowerCase(Locale.getDefault()).contains(lowercaseQuery)
        }
    }

    // Implementación de la interfaz para manejar los cambios en el estado de guardado del juego
    override fun onGameSavedChanged(game: Game) {
        // Maneja el cambio en el estado de guardado del juego
        val managerSavedGames = ManagerSavedGames(requireContext())
        if (game.isSaved) {
            managerSavedGames.saveGame(game)
        } else {
            managerSavedGames.unsaveGame(game)
        }
    }
}

