package com.nasupe.gamequest

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

class FourthFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Realizar la acción de cerrar sesión y redirigir al usuario
        cerrarSesion()
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        // Redirigir al usuario a la pantalla de inicio de sesión o a donde desees
        val intent = Intent(requireContext(), Home::class.java)
        startActivity(intent)
        requireActivity().finish() // Cierra la actividad actual para evitar volver atrás
    }
}

