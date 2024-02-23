package com.javierprado.jmapp.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.AuthResponse
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.MenuPrincipalApoderadoActivity
import com.javierprado.jmapp.view.ResetPasswordActivity
import com.javierprado.jmapp.view.menu_apoderado
import retrofit2.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.btn_inicioSesion)
        val olvidasteContrasena = findViewById<TextView>(R.id.olvidasteContrasena)

        mAuth = FirebaseAuth.getInstance()

        // Deshabilitar el botón de inicio de sesión al inicio
        btnLogin.isEnabled = false

        // Agregar TextWatchers a los EditTexts
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        btnLogin.setOnClickListener {
            val emailUser = emailEditText.text.toString().trim()
            val passUser = passwordEditText.text.toString().trim()

            if (emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
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

    private fun loginUser(emailUser: String, passUser: String) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                    startActivity(Intent(this@LoginActivity, menu_apoderado::class.java))
                    Toast.makeText(this@LoginActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
                    val usuario = Usuario(emailUser, passUser)

                    val api: ColegioAPI = RetrofitHelper.getInstanceStatic().getApi()

                    var msg : String
                    api.login(usuario)?.enqueue(object : Callback<AuthResponse?> {
                        override fun onResponse(call: Call<AuthResponse?>?, response: Response<AuthResponse?>) {
                            if (response.isSuccessful) {
                                msg = response.body()?.tokenDeAcceso.toString()
                                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                                finish()
                                startActivity(Intent(this@LoginActivity, MenuPrincipalApoderadoActivity::class.java))
                            }
                        }

                        override fun onFailure(call: Call<AuthResponse?>?, t: Throwable?) {
                            msg = "Error en el API y no en el Firebase."

                            Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    })
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