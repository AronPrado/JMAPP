package com.javierprado.jmapp.view.activities.editar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActualizarInfoApoderadoActivity : AppCompatActivity() {
    private lateinit var correoEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var btnActualizar: Button

    private lateinit var progressBar: CircularProgressIndicator

    private lateinit var auth: FirebaseAuth
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_info_apoderado)

        auth = FirebaseAuth.getInstance()

        correoEditText = findViewById(R.id.edtEmailApoderado)
        telefonoEditText = findViewById(R.id.edtTelefono)
        direccionEditText = findViewById(R.id.edtDireccion)
        btnActualizar = findViewById(R.id.btnActualizar)

        progressBar = findViewById(R.id.pb_info_apoderado)

        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            retro.setBearerToken(token)
        }
        val api = retro.getApi()

        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@ActualizarInfoApoderadoActivity, MenuApoderadoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this@ActualizarInfoApoderadoActivity, MenuApoderadoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        var msg : String? = ""

        progressBar.visibility= View.VISIBLE
        api.obtenerSesion(RoleType.APOD.name).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val user : Usuario = response.body()!!
                    correoEditText.setText(user.email)
                    telefonoEditText.setText(user.telefono.toString())
                    direccionEditText.setText(user.apoderado.direccion.toString())
                }else{
                    msg = response.headers()["message"] ?: ""
                    Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility= View.GONE
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                msg = "Servidor desconectado"
                Log.e(msg, t.message.toString())
                Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
            }
        })
        btnActualizar.setOnClickListener{
            val nuevoCorreo = correoEditText.text.toString()
            val nuevoTelefono = telefonoEditText.text.toString().toInt()
            val nuevaDireccion = direccionEditText.text.toString()

            val apoderado = Apoderado(nuevoCorreo, nuevoTelefono, nuevaDireccion)
            api.actualizarInfoApoderado(apoderado)?.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers().get("message")
                    if (response.isSuccessful) {
                        val intent = Intent(this@ActualizarInfoApoderadoActivity, MenuApoderadoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    }else{
                        Log.e("ERROR AL ACTUALIZAR", msg ?: "")
                    }
                    Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL ACTUALIZAR", t.message.toString())
                }
            })
        }
    }
}