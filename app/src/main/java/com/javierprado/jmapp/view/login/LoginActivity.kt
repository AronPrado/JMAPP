package com.javierprado.jmapp.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.view.activities.editar.ResetPasswordActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLogin: Button
    private var extraF: ExtraFunctions = ExtraFunctions()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


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

//            extraF.crearCanalNoti(this)
//            extraF.crearNoti("PRUEBA CUERPO", "TITULO GRANDE", this)

            if (emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(this, "Ingresar los datos", Toast.LENGTH_SHORT).show()
            } else {
                authFunctions.iniciarVerificacionRecaptcha(emailUser, passUser, RoleType.APOD.name, this, MenuApoderadoActivity())
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