package com.javierprado.jmapp.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.javierprado.jmapp.R
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity

class OptionLogin : AppCompatActivity() {
    val ROL="rolusaurio"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_login)

        // Obteniendo referencias a los layouts de Administrador, Apoderado y Docente
        val lyAdmin = findViewById<LinearLayout>(R.id.lyAdmin)
        val lyApoderado = findViewById<LinearLayout>(R.id.lyApoderado)
        val lyDocente = findViewById<LinearLayout>(R.id.lyDocente)

        // Agregando listeners de clics a los layouts
        lyAdmin.setOnClickListener {
            // Abrir la actividad de login para el Administrador
            val intent = Intent(this@OptionLogin, LoginAdmin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        lyApoderado.setOnClickListener {
            // Abrir la actividad de login para el Apoderado
            val intent = Intent(this@OptionLogin, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        lyDocente.setOnClickListener {
            // Abrir la actividad de login para el Docente
            val intent = Intent(this@OptionLogin, LoginDocente::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
}