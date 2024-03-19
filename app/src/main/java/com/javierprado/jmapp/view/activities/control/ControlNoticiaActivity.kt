package com.javierprado.jmapp.view.activities.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class ControlNoticiaActivity : AppCompatActivity() {
    private lateinit var tituloText: EditText
    private lateinit var contenidoText: EditText
    private lateinit var tipoCmb: Spinner

    private lateinit var btnControl: Button

    private lateinit var api : ColegioAPI
    private lateinit var msg : String
    val TOKEN = "token"
    val NOTID = "noticiaId"
    var noticiaId : Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_noticia)

        var toAdd = false

        tituloText = findViewById(R.id.txt_titulo_noticia)
        contenidoText = findViewById(R.id.txt_contenido_noticia)
        tipoCmb = findViewById(R.id.s_tipo_noticia)

        btnControl = findViewById(R.id.btn_control_noticia)

        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            noticiaId = bundle.getInt(NOTID, 0)
            toAdd = noticiaId == 0
            retro.setBearerToken(token)
        }
        api = retro.getApi()

        establecerModo(toAdd)

        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@ControlNoticiaActivity, MenuAdministradorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
    private fun manejarNoticia(toAdd: Boolean) {
        val titulo = tituloText.text.toString().trim()
        val contenido = contenidoText.text.toString().trim()
        val ubiImagen  = obtenerImagen(tipoCmb.selectedItemPosition + 1)

        api.obtenerSesion(RoleType.ADMIN.name).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                msg = "Usuario no encontrado."
                if (response.isSuccessful) {
                    val noticia = Noticia(titulo, contenido, ubiImagen, administrador = response.body()?.administrador!!)
                    if(toAdd){
                        api.agregarNoticia(noticia).enqueue(object : Callback<Noticia> {
                            override fun onResponse(call: Call<Noticia>, response: Response<Noticia>) {
                                msg = response.headers()["message"] ?: ""
                                if (response.isSuccessful) {
                                    val noticiaId = response.body()?.noticiaId
                                    msg = "ID de noticia: ${noticiaId}"
                                } else{ Log.e("NR NOTICIA", msg) }
                                Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                            override fun onFailure(call: Call<Noticia>, t: Throwable) {
                                msg = "Error en la API: ${t.message}"
                                Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                                Log.e("AGREGAR NOTICIA", t.message.toString())
                            }
                        } )
                    }else{
                        noticia.noticiaId=noticiaId
                        api.editarNoticiaPorId(noticia).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                msg = response.headers()["message"] ?: ""
                                Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@ControlNoticiaActivity, MenuAdministradorActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                msg = "Error en la API: ${t.message}"
                                Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                                Log.e("EDITAR NOTICIA", t.message.toString())
                            }
                        } )
                    }
                } else{
                    Log.e("NR SESSION", msg)
                    Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                Log.e("BUSCAR SESSION", t.message.toString())
            }
        } )
    }

    private fun obtenerImagen(indice: Int): String {
        val random = Random.nextInt(1, 4)
        val img = "noti_${indice}_${random}"
        return img
    }
    private fun establecerModo(toAdd: Boolean) {
        val txtTitulo = findViewById<TextView>(R.id.tv_title_control_noticia)
        val tipo = if (toAdd) "Agregar noticia" else "Editar noticia"
        txtTitulo.text = tipo
        btnControl.text = tipo
        btnControl.setOnClickListener {
            manejarNoticia(toAdd)
        }
        if(!toAdd){
            val progressBarListar: CircularProgressIndicator = findViewById(R.id.pb_control_noticia)
            progressBarListar.visibility = View.VISIBLE
            noticiaId?.let {
                api.obtenerNoticiaPorId(it).enqueue(object : Callback<Noticia> {
                    override fun onResponse(call: Call<Noticia>, response: Response<Noticia>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val noticia = response.body()!!
                            tituloText.setText(noticia.titulo)
                            contenidoText.setText(noticia.contenido)
                            if(noticia.ubiImagen?.isNotEmpty() == true){
                                tipoCmb.setSelection(noticia.ubiImagen?.replace("noti_","")?.get(0).toString().toInt() - 1)
                            }else{
                                tipoCmb.setSelection(0)
                            }

                        } else{ Log.e("NR NOTICIA", msg) }
                        Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                        progressBarListar.visibility=View.GONE
                    }

                    override fun onFailure(call: Call<Noticia>, t: Throwable) {
                        msg = "Error en la API"
                        Toast.makeText(this@ControlNoticiaActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("BUSCAR NOTICIA", t.message.toString())
                    }
                } )
            }
        }
    }
}