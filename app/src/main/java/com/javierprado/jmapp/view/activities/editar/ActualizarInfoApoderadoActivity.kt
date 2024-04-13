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
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
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
    var tokenApod = ""; var apoderadoId = ""
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
            tokenApod = bundle.getString(MenuAdministradorActivity().TOKEN, "")
            retro.setBearerToken(tokenApod)
            apoderadoId = bundle.getString(MenuAdministradorActivity().USUARIOID, "")
        }
        val api = retro.getApi()

        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener { regresar() }
        val btnCancelar: Button = findViewById(R.id.btnCancelar)
        btnCancelar.setOnClickListener { regresar() }

        var msg : String
        progressBar.visibility= View.VISIBLE
        api.buscarApoderado(apoderadoId).enqueue(object : Callback<Apoderado> {
            override fun onResponse(call: Call<Apoderado>, response: Response<Apoderado>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val apoderado = response.body()!!
                    correoEditText.setText(apoderado.correo)
                    telefonoEditText.setText(apoderado.telefono.toString())
                    direccionEditText.setText(apoderado.direccion)
                }else{
                    Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL BUSCAR", msg)
                    regresar()
                }
                progressBar.visibility= View.GONE
            }

            override fun onFailure(call: Call<Apoderado>, t: Throwable) {
                msg = "Servidor desconectado."
                Log.e(msg, t.message.toString())
                Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
            }
        })
        btnActualizar.setOnClickListener{
            val nuevoCorreo = correoEditText.text.toString()
            val nuevoTelefono = telefonoEditText.text.toString().toInt()
            val nuevaDireccion = direccionEditText.text.toString()

            val apoderado = Apoderado(nuevoCorreo, nuevoTelefono, nuevaDireccion)
            api.guardarApoderado(apoderado, apoderadoId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) { regresar() }
                    else{
                        Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("ERROR AL ACTUALIZAR", msg)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(this@ActualizarInfoApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL ACTUALIZAR", t.message.toString())
                }
            })
        }
    }
    private fun regresar(){
        val intent = Intent(this@ActualizarInfoApoderadoActivity, MenuApoderadoActivity::class.java)
        intent.putExtra(MenuAdministradorActivity().USUARIOID, apoderadoId)
        intent.putExtra(MenuAdministradorActivity().TOKEN, tokenApod)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent) ; finish()
    }
}