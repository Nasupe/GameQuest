package com.nasupe.gamequest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicialización de Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Obtención de referencias a las vistas
        val correoEditText = findViewById<EditText>(R.id.txt_registrocorreo)
        val contrasenaEditText = findViewById<EditText>(R.id.txt_registropass)
        val registrarseButton = findViewById<Button>(R.id.bt_registro)
        val volverButton = findViewById<Button>(R.id.bt_volver3)

        // Configuración del listener para el botón "Registrarse"
        registrarseButton.setOnClickListener {
            // Obtención del correo electrónico y la contraseña ingresados por el usuario
            val correo = correoEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()

            // Verificar si los campos están en blanco
            if (correo.isBlank() || contrasena.isBlank()) {
                // Mostrar un mensaje de error indicando que los campos no pueden estar en blanco
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT)
                    .show()
            } else if (!isValidEmail(correo)) {
                // Mostrar un mensaje de error indicando que el correo electrónico no es válido
                Toast.makeText(
                    this,
                    "Por favor, introduce un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!isValidPass(contrasena)) {
                // Mostrar un mensaje de error indicando que la contraseña no cumple con los requisitos
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 5 caracteres y  al menos un número.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Llamada al método de registro con Firebase Authentication
                signUp(correo, contrasena)
            }
        }

        // Configuración del listener para el botón "Volver"
        volverButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    // Método para registrar un nuevo usuario con Firebase Authentication
    private fun signUp(correo: String, contrasena: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, mostrar un AlertDialog y dirigir al usuario a la actividad Home
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Éxito")
                    builder.setMessage("Registro exitoso.")
                    builder.setPositiveButton("OK") { _, _ ->
                        val intent = Intent(this, Iniciosesion::class.java)
                        startActivity(intent)
                        finish() // Cerramos la actividad actual
                    }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    // Error en el registro, mostrar un AlertDialog con el mensaje de error correspondiente
                    val message = if (task.exception is FirebaseAuthUserCollisionException) {
                        "El correo electrónico ya está en uso. Por favor, usa otro correo electrónico."
                    } else {
                        "Error en el registro. Por favor, intenta nuevamente."
                    }
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Error")
                    builder.setMessage(message)
                    builder.setPositiveButton("OK", null)
                    val dialog = builder.create()
                    dialog.show()
                }
            }
    }

    // Método para validar el formato del correo electrónico utilizando una expresión regular
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
        return emailRegex.matches(email)
    }

    private fun isValidPass(password: String): Boolean {
        val passRegex = Regex("^(?=.*[0-9]).{5,}\$")
        return passRegex.matches(password)
    }
}