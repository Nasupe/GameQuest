package com.nasupe.gamequest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nasupe.gamequest.JsonUtils.addGameToJson
import kotlinx.coroutines.withContext

class FifthFragment : Fragment(), GamesAdapter.OnGameSavedChangedListener, OnGameAddListener{
    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var gamesAdapter: GamesAdapter
    private lateinit var gamesList: MutableList<Game> // Cambiado a MutableList para permitir modificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fifth, container, false)
        Log.d("FifthFragment" ,"comprobanfdo el contexto" + context.toString())

        // Inicializar RecyclerView
        gamesRecyclerView = view.findViewById(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(context)
        gamesRecyclerView.setHasFixedSize(true) // Optimización si el tamaño del RecyclerView no cambiará

        // Leer el archivo JSON y parsear los juegos
        val jsonString = JsonUtils.readJsonFromFile(requireContext())
        Log.d("FifthFragment", "JSON leído desde el archivo: $jsonString")
        gamesList = JsonUtils.parseJsonToGames(jsonString ?: "").toMutableList() // Convertir a MutableList
        Log.d("FifthFragment", "Lista de juegos obtenida del JSON: $gamesList")

        // Configurar el adaptador y asignarlo al RecyclerView
        gamesAdapter = GamesAdapter(gamesList, this)
        gamesRecyclerView.adapter = gamesAdapter

        // Obtener una referencia al botón de agregar recurso
        val addResourceButton = view.findViewById<ImageButton>(R.id.addResourceButton)

        // Configurar el OnClickListener para el botón de agregar recurso
        addResourceButton.setOnClickListener {
            // Crear un Intent para iniciar la AddResourcesActivity
            val intent = Intent(requireContext(), AddResourcesActivity::class.java)
            Log.d("FifthFragment" ,"checking" + requireContext().toString())
            startActivity(intent)
        }

        return view
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            FifthFragment()
    }

    override fun onGameSavedChanged(game: Game) {
        // Obtener el índice del juego en la lista
        val index = gamesList.indexOfFirst { it.nombre == game.nombre }

        if (index != -1) { // Verificar si el juego está en la lista
            gamesList[index] = game // Actualizar el juego en la lista

            // Actualizar el RecyclerView
            gamesAdapter.notifyItemChanged(index)

            // Manejar el cambio en el estado de guardado del juego
            val managerSavedGames = ManagerSavedGames(requireContext())
            if (game.isSaved) {
                managerSavedGames.saveGame(game)
            } else {

            }
        }
    }

    override fun gameAddListener(game: String) {
        if (context != null) {
            addGameToJson(game, requireContext())
        } else {
            Log.e("FifthFragment", "El Fragmento no está adjunto a una actividad o el contexto es nulo.")
        }

    }

}
