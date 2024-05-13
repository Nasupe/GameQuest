package com.nasupe.gamequest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Iniciosesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciosesion)

        // Inicialización de Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Obtención de referencias a las vistas
        val correoEditText = findViewById<EditText>(R.id.txt_iniciocorreo)
        val contrasenaEditText = findViewById<EditText>(R.id.txt_iniciopass)
        val iniciarSesionButton = findViewById<Button>(R.id.bt_logearse)
        val volverButton = findViewById<Button>(R.id.bt_volver2)

        // Configuración del listener para el botón "Iniciar sesión"
        iniciarSesionButton.setOnClickListener {
            // Obtención del correo electrónico y la contraseña ingresados por el usuario
            val correo = correoEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()

            // Verificar si los campos están en blanco
            if (correo.isBlank() || contrasena.isBlank()) {
                // Mostrar un mensaje de error: los campos no pueden estar en blanco
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Llamada al método de inicio de sesión con Firebase Authentication
                signIn(correo, contrasena)
            }
        }

        // Configuración del listener para el botón Volver
        volverButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    // Método para iniciar sesión con Firebase Authentication
    private fun signIn(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val user = auth.currentUser
                    if (user != null) {
                        // El usuario está registrado, reedirigimos a otra activity
                        startActivity(Intent(this, Feed::class.java))
                        finish()
                    }
                } else {
                    // Error en el inicio de sesión
                    val exception = task.exception
                    when {

                        exception is FirebaseAuthInvalidCredentialsException -> {
                            // La contraseña no coincide con el correo electrónico
                            showAlert("Error", "Asegurate de que el correo electrónico y la contraseña sean válidos")
                        }
                        else -> {
                            // Otro error en el inicio de sesión
                            showAlert("Error", "Inicio de sesión fallido.")
                        }
                    }
                }
            }
    }

    // Método para mostrar un diálogo de alerta
    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }
}
