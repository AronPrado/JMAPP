package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.javierprado.jmapp.R

class OptionLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_login)
    }

    fun iniciarSesionApoderado(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun iniciarSesionDocente(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun iniciarSesionAdministrador(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}