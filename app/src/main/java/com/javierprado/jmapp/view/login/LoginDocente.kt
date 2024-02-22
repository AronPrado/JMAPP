package com.javierprado.jmapp.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.view.ResetPasswordActivity
import com.javierprado.jmapp.view.menu_administrador
import com.javierprado.jmapp.view.menu_docente

class LoginDocente : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_docente)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val btn_login = findViewById<Button>(R.id.btn_inicioSesion)
        val olvidasteContrasena = findViewById<TextView>(R.id.olvidasteContrasena)

        mAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            val emailUser = emailEditText.text.toString().trim()
            val passUser = passwordEditText.text.toString().trim()

            if (emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(this@LoginDocente, "Ingresar los datos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(emailUser, passUser)
            }
        }

        olvidasteContrasena.setOnClickListener {
            val intent = Intent(this@LoginDocente, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(emailUser: String, passUser: String) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                    startActivity(Intent(this@LoginDocente, menu_docente::class.java))
                    Toast.makeText(this@LoginDocente, "Bienvenido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginDocente, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@LoginDocente, "Error al iniciar sesión", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}