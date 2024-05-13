package com.nasupe.gamequest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddResourcesActivity : AppCompatActivity() {

    private var onGameAddListener: OnGameAddListener? = null //Variable de la interfaz
    private val categorias = arrayOf(
        "Juegos nocturnos",
        "Juegos de interior",
        "Juegos de exterior",
        "Juegos de presentación",
        "Juegos cooperativos",
        "Juegos competitivos",
        "Juegos por equipos",
        "Juegos de distensión",
        "Juegos de confianza",
        "Danzas",
        "Resolución de conflictos"
    )
    private var juegoNuevo = ""

    // Inicializar el array categoriasSeleccionadas con la misma longitud que categorias
    private var categoriasSeleccionadas = BooleanArray(categorias.size)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resources)

        onGameAddListener = FifthFragment() //Se iguala a la activity o fragment que la tenga implementada
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etMateriales = findViewById<EditText>(R.id.etMateriales)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val btnSeleccionarCategorias = findViewById<Button>(R.id.btnSeleccionarCategorias)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        //Boton atrás
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnSeleccionarCategorias.setOnClickListener {
            mostrarDialogoSeleccionMultiple()
        }

        btnAgregar.setOnClickListener{
                        juegoNuevo = " {\n" +
                    "      \"nombre\": \"${etNombre.text}\",\n" +
                    "      \"materiales\": \"${etMateriales.text }\",\n" +
                    "      \"categoria\": \"${etDescripcion.text}\",\n" +
                    "      \"descripcion\": \"${btnSeleccionarCategorias.text}\"\n}"

            (onGameAddListener as FifthFragment).gameAddListener(juegoNuevo) // Llamar al metodo de la interfaz que necesito
            finish()
        }
    }

    private fun mostrarDialogoSeleccionMultiple() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona las categorías")

        builder.setMultiChoiceItems(categorias, categoriasSeleccionadas) { _, which, isChecked ->
            categoriasSeleccionadas[which] = isChecked
        }

        builder.setPositiveButton("Aceptar") { _, _ ->
            actualizarTextoBoton()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun actualizarTextoBoton() {
        val btnSeleccionarCategorias = findViewById<Button>(R.id.btnSeleccionarCategorias)
        val categoriasSeleccionadasList = mutableListOf<String>()
        for (i in categorias.indices) {
            if (categoriasSeleccionadas[i]) {
                categoriasSeleccionadasList.add(categorias[i])
            }
        }
        btnSeleccionarCategorias.text = categoriasSeleccionadasList.joinToString(", ")
    }
}



