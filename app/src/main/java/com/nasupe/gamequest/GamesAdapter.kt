package com.nasupe.gamequest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GamesAdapter(private var games: List<Game>,
                   private val listener: OnGameSavedChangedListener) :
    RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameNameTextView: TextView = itemView.findViewById(R.id.gameNameTextView)
        val gameCategoryTextView: TextView = itemView.findViewById(R.id.gameCategoryTextView)
        val gameMaterialsTextView: TextView = itemView.findViewById(R.id.gameMaterialsTextView)
        val gameDescriptionTextView: TextView = itemView.findViewById(R.id.gameDescriptionTextView)
        val saveIcon: ImageView = itemView.findViewById(R.id.saveIcon)

        init {
            // Agregar un listener al ícono de guardar para manejar el clic
            saveIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val game = games[position]
                    // Cambiar el estado de guardado del juego
                    game.isSaved = !game.isSaved
                    // Notificar al listener sobre el cambio en el estado de guardado
                    listener?.onGameSavedChanged(game)
                    // Actualizar la vista del ícono de guardar
                    notifyItemChanged(position)


                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_item, parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val currentGame = games[position]
        holder.gameNameTextView.text = currentGame.nombre
        holder.gameCategoryTextView.text = "Categoría: ${currentGame.categoria}"
        holder.gameMaterialsTextView.text = "Materiales: ${currentGame.materiales}"
        holder.gameDescriptionTextView.text = "Descripción: ${currentGame.descripcion}"

        // Cambiar el ícono de guardar según el estado de guardado del juego
        if (currentGame.isSaved) {
            holder.saveIcon.setImageResource(R.drawable.icon_saved)
        } else {
            holder.saveIcon.setImageResource(R.drawable.icon_not_saved)
        }
    }

    override fun getItemCount() = games.size

    // Método para actualizar la lista de juegos guardados
    fun updateSavedGames(newSavedGames: List<Game>) {
        games = newSavedGames
        notifyDataSetChanged()
    }


    interface OnGameSavedChangedListener {
        fun onGameSavedChanged(game: Game)
    }
}
