package com.javierprado.jmapp.view.login

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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