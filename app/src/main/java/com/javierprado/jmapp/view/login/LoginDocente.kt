package com.javierprado.jmapp.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.view.ResetPasswordActivity
import com.javierprado.jmapp.view.menus.menu_docente

class LoginDocente : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_docente)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.btn_inicioSesion)
        val olvidasteContrasena = findViewById<TextView>(R.id.olvidasteContrasena)

        val authFunctions = AuthFunctions()
        // Deshabilitar el botón de inicio de sesión al inicio
        btnLogin.isEnabled = false
        // Agregar TextWatchers a los EditTexts
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        btnLogin.setOnClickListener {
            val emailUser = emailEditText.text.toString().trim()
            val passUser = passwordEditText.text.toString().trim()

            if (emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                authFunctions.loginUser(emailUser, passUser, RoleType.DOC.name, this, menu_docente())
            }
        }

        olvidasteContrasena.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val emailInput = emailEditText.text.toString().trim()
            val passwordInput = passwordEditText.text.toString().trim()

            // Habilitar el botón de inicio de sesión si ambos campos no están vacíos
            btnLogin.isEnabled = emailInput.isNotEmpty() && passwordInput.isNotEmpty()
        }
    }
}