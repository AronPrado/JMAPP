package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.AuthResponse
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import retrofit2.Callback

class activity_register_student : AppCompatActivity() {
    private lateinit var nombres: EditText
    private lateinit var apellidos: EditText
    private lateinit var correo: EditText
    private lateinit var telefono: EditText
    private lateinit var direccion: EditText
    private lateinit var btnRegistrar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)

        btnRegistrar = findViewById(R.id.btnRegistrar)

        val token = ""

        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@activity_register_student, menu_administrador::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener{
            val nombres = nombres.text.toString().trim()
            val apellidos = apellidos.text.toString().trim()
            val correo = correo.text.toString().trim()
            val telefono = telefono.text.toString().trim()
            val direccion = direccion.text.toString().trim()

            var estudiante : Estudiante

            val retro = RetrofitHelper.getInstanceStatic()
            retro.setBearerToken(token)
            val api = retro.getApi()
        }


    }
}