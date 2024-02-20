package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import com.javierprado.jmapp.R

class docente_main : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente_main)

        val navigationView = findViewById<NavigationView>(R.id.nav_view_docente)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_secciones -> {
                startActivity(Intent(this, Seleccion_Sesion_Activity::class.java))
            }
            R.id.nav_Apoderado -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
    }

        return true
    }
}