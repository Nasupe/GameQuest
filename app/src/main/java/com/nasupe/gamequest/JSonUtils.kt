package com.nasupe.gamequest

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object JsonUtils {

    var juegosJson = ""
    var nombreFichero = "Juegos.json"

    // Método para leer el contenido de un archivo JSON desde los recursos de la aplicación
    fun readJsonFromFile(context: Context): String? {
        juegosJson = ""
        return try {
            // Abrir el archivo JSON desde la carpeta de activos (assets)
            val inputStream = context.assets.open(nombreFichero)
            // Obtener el tamaño del archivo
            val size = inputStream.available()
            // Crear un buffer para almacenar los datos del archivo
            val buffer = ByteArray(size)
            // Leer los datos del archivo y almacenarlos en el buffer
            inputStream.read(buffer)
            // Cerrar el flujo de entrada
            inputStream.close()
            // Convertir el buffer a una cadena y devolverla
            val jsonString = String(buffer, Charsets.UTF_8)
            Log.d("JsonUtils", "Contenido del archivo JSON: $jsonString")
            juegosJson = jsonString
            jsonString
        } catch (e: Exception) {
            // Manejar cualquier excepción que pueda ocurrir durante la lectura del archivo
            e.printStackTrace()
            null
        }
    }

    fun addGameToJson(newGame: String, context: Context) {
        context?.let {
            val inputStream: InputStream = context.assets.open(nombreFichero)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val existingJson = bufferedReader.use { it.readText() }
            val jsonObject = JSONObject(existingJson)
            val jsonArray = jsonObject.getJSONArray("juegos")
            val newGameJson = JSONObject(newGame)
            jsonArray.put(newGameJson)

            try {
                val outputStreamWriter =
                    OutputStreamWriter(context.openFileOutput(nombreFichero, Context.MODE_PRIVATE))
                outputStreamWriter.write(jsonObject.toString())
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: $e")
            }
        }
    }

    // Método para analizar una cadena JSON y convertirla en una lista de objetos Game
    fun parseJsonToGames(jsonString: String): List<Game> {
        // Lista para almacenar los objetos Game
        val gamesList = mutableListOf<Game>()
        try {
            // Crear un JSONObject a partir de la cadena JSON
            val jsonObject = JSONObject(jsonString)
            // Obtener el JSONArray que contiene los juegos
            val jsonArray = jsonObject.getJSONArray("juegos")
            // Iterar sobre cada objeto JSON en el JSONArray
            for (i in 0 until jsonArray.length()) {
                // Obtener el objeto JSON actual
                val jsonObject = jsonArray.getJSONObject(i)
                // Extraer los campos del objeto JSON y crear un objeto Game
                val game = Game(
                    jsonObject.getString("nombre"),
                    jsonObject.getString("materiales"),
                    jsonObject.getString("categoria"),
                    jsonObject.getString("descripcion")
                )
                // Agregar el objeto Game a la lista
                gamesList.add(game)
            }
        } catch (e: JSONException) {
            // Manejar cualquier excepción que pueda ocurrir durante el análisis del JSON
            e.printStackTrace()
        }
        // Devolver la lista de objetos Game
        return gamesList
    }

}
