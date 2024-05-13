package com.nasupe.gamequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class Feed : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Buscar el BottomNavigationView por su ID
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Establecer un listener para las selecciones de elementos en el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.firstFragment -> loadFragment(FirstFragment())
                R.id.secondFragment -> loadFragment(SecondFragment())
                R.id.thirdFragment -> loadFragment(ThirdFragment())
                R.id.fourthFragment -> loadFragment(FourthFragment())
                R.id.fifthFragment -> loadFragment(FifthFragment())
            }
            true
        }

        // Mostrar inicialmente el FifthFragment al iniciar la actividad
        loadFragment(FifthFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = this.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
