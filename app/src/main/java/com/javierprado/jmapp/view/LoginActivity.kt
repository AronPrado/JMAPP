package com.javierprado.jmapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val btn_login = findViewById<Button>(R.id.btn_inicioSesion)
        val olvidasteContrasena = findViewById<TextView>(R.id.olvidasteContrasena)

        mAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            val emailUser = emailEditText.text.toString().trim()
            val passUser = passwordEditText.text.toString().trim()

            if (emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Ingresar los datos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(emailUser, passUser)
            }
        }

        olvidasteContrasena.setOnClickListener {
            val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(emailUser: String, passUser: String) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                    startActivity(Intent(this@LoginActivity, docente_main::class.java))
                    Toast.makeText(this@LoginActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@LoginActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}