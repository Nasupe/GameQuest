package com.nasupe.gamequest

data class Game(
    val nombre: String,
    val materiales: String,
    val categoria: String,
    val descripcion: String,
    var isSaved: Boolean = false // Propiedad para indicar si el juego está guardado

)