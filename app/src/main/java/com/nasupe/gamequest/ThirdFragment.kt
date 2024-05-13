package com.nasupe.gamequest


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ThirdFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var editTextOldPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSubject: EditText
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        editTextOldPassword = view.findViewById(R.id.editTextOldPassword)
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextSubject = view.findViewById(R.id.editTextSubject)
        editTextMessage = view.findViewById(R.id.editTextMessage)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el click listener para el botón de cambiar contraseña
        view.findViewById<Button>(R.id.buttonChangePassword).setOnClickListener {
            changePassword()
        }
        // Configurar el click listener para el botón de enviar correo electrónico
        view.findViewById<Button>(R.id.buttonSend).setOnClickListener {
            sendEmail()
        }
    }

    private fun changePassword() {
        val oldPassword = editTextOldPassword.text.toString()
        val newPassword = editTextNewPassword.text.toString()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)

            currentUser.reauthenticate(credential)
                .addOnSuccessListener {
                    if (newPassword.matches(Regex("^(?=.*[0-9]).{5,}$"))) {
                        currentUser.updatePassword(newPassword)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Contraseña cambiada exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Error al cambiar la contraseña: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "La nueva contraseña debe tener al menos 5 caracteres y contener al menos un número",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error de autenticación: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val email = editTextEmail.text.toString()
        val subject = editTextSubject.text.toString()
        val message = editTextMessage.text.toString()

        // Verificar que todos los campos estén completos
        if (email.isBlank() || subject.isBlank() || message.isBlank()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("gamequestsuppport@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        // Verificar si hay una aplicación que pueda manejar el intento de enviar correo electrónico
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
            // Limpiar los campos después de enviar el correo
            editTextEmail.text.clear()
            editTextSubject.text.clear()
            editTextMessage.text.clear()
        } else {
            // Si no se encuentra ninguna aplicación para manejar el intento de enviar correo electrónico
            Toast.makeText(requireContext(), "No se encontró ninguna aplicación de correo electrónico.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
