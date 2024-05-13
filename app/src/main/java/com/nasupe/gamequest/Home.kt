package com.nasupe.gamequest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.nasupe.gamequest.Registro



class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val botonInicioSesion = findViewById<Button>(R.id.bt_iniciosesion)
        botonInicioSesion.setOnClickListener {
            val intent = Intent(this, Iniciosesion::class.java)
            startActivity(intent)
        }

        val botonRegistro = findViewById<Button>(R.id.bt_registarse)
        botonRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }
}

