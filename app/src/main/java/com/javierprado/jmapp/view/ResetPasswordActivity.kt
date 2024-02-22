package com.javierprado.jmapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.view.login.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var recuperarBoton: Button
    private lateinit var emailEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        recuperarBoton = findViewById(R.id.recuperarBoton)
        emailEditText = findViewById(R.id.emailEditText)

        recuperarBoton.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Correo inválido"
            return
        }

        sendEmail(email)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun sendEmail(email: String) {
        val auth = FirebaseAuth.getInstance()
        val emailAddress = email

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Correo enviado!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Correo inválido", Toast.LENGTH_SHORT).show()
                }
            }
    }
}