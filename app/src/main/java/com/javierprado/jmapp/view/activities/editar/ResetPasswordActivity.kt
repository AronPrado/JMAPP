package com.javierprado.jmapp.view.activities.editar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.view.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var recuperarBoton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passEditText: EditText

    private lateinit var api : ColegioAPI
    private lateinit var auth: FirebaseAuth
    private lateinit var msg : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        recuperarBoton = findViewById(R.id.recuperarBoton)
        emailEditText = findViewById(R.id.txt_email_reset)
        passEditText = findViewById(R.id.txt_pass_reset)

        val retro = RetrofitHelper.getInstanceStatic()
        api = retro.getApi()
        auth = FirebaseAuth.getInstance()

        recuperarBoton.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        val email = emailEditText.text.toString().trim()
        val newPass = "cambiarContra123"

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Correo inválido"
            return
        }
        val progresDialog = AuthFunctions().mostrarCarga(this@ResetPasswordActivity, "Comprobando correo.")
        progresDialog.show()
        api.existeCorreo(email).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                msg = "EL CORREO NO EXISTE"
                if (response.isSuccessful) {
                    auth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful ) {
                                sendEmail(email)
                                msg = "Correo para restablecer contraseña enviado."
                                val usuario = Usuario(email, newPass, "")
                                api.actualizarContrasena(usuario).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        progresDialog.dismiss()
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        progresDialog.dismiss()
                                        msg = "Error en la API: ${t.message}"
                                        Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_SHORT).show()
                                        Log.e("ERROR CONTRASEÑA", t.message.toString())
                                    }
                                } )
                            } else {
                                progresDialog.dismiss()
                                msg+=" EN FIREBASE"
                                Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else{
                    progresDialog.dismiss()
                    Log.e("NR COMPROBAR CORREO", msg) }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_SHORT).show()
                Log.e("ERROR COMPROBAR CORREO", t.message.toString())
            }
        } )
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