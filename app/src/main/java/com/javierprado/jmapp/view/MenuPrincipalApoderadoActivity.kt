package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import com.javierprado.jmapp.R
class MenuPrincipalApoderadoActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp_apoderado)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, Seleccion_Sesion_Activity::class.java))
            }
            R.id.nav_trabajos -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
            R.id.nav_recursos -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
            R.id.nav_horario -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
            R.id.nav_eventos -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
            R.id.nav_docentes -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
            R.id.nav_comunicación -> {
                startActivity(Intent(this, MensajeDocente::class.java))
            }
        }

        return true
    }
}