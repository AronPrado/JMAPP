package com.javierprado.jmapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AuthFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_register_student : AppCompatActivity() {
    private lateinit var nombresEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var correoEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var spinner: Spinner

    private lateinit var auth: FirebaseAuth
    private lateinit var estudiantes : Collection<Estudiante>
    private val authFunctions = AuthFunctions()
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)

        auth = FirebaseAuth.getInstance()

        nombresEditText = findViewById(R.id.edtNombres)
        apellidosEditText = findViewById(R.id.edtApellidos)
        correoEditText = findViewById(R.id.edtEmailApoderado)
        telefonoEditText = findViewById(R.id.edtTelefono)
        direccionEditText = findViewById(R.id.edtDireccion)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        spinner = findViewById(R.id.spinner)

        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            retro.setBearerToken(token)
        }
        val api = retro.getApi()

        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@activity_register_student, menu_administrador::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        var msg : String? = ""
        api.obtenerEstudiantes(null, null, null)?.enqueue(object : Callback<Collection<Estudiante>>{
            override fun onResponse(call: Call<Collection<Estudiante>>, response: Response<Collection<Estudiante>>) {
                msg = response.headers().get("message")
                if (response.isSuccessful) {
                    estudiantes = response.body()!!
                    val nombresEstudiantes = estudiantes.map { it.nombres + " " + it.apellidos }
                    val adaptador = ArrayAdapter(this@activity_register_student, android.R.layout.simple_spinner_item, nombresEstudiantes)
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adaptador
                } else{ Log.e("LISTAR ESTUDIANTES:", msg?:"") }
                Toast.makeText(this@activity_register_student, msg, Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<Collection<Estudiante>>, t: Throwable) {
                Log.e("LISTAR ESTUDIANTES ERROR", t.message.toString())
            }
        })

        btnRegistrar.setOnClickListener{
            val nombres = nombresEditText.text.toString().trim()
            val apellidos = apellidosEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()

            val nombreSeleccionado = spinner.selectedItem as String
            val estudianteSeleccionado = estudiantes.find { it.nombres + " " + it.apellidos == nombreSeleccionado }

            var estudiantesApoderado = HashSet<Estudiante?>()
            estudiantesApoderado.add(estudianteSeleccionado)

            val apoderado = Apoderado(nombres, apellidos, correo, telefono.toInt(), direccion, estudiantesApoderado)

            api.agregarApoderado(apoderado).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    msg = response.headers().get("message")
                    if (response.isSuccessful) {
                        msg = response.body().toString()
                        auth.createUserWithEmailAndPassword(correo, telefono)
                            .addOnCompleteListener { task: Task<AuthResult?> ->
                                if (task.isSuccessful) {
                                    authFunctions.enviarCredenciales(correo, telefono, this@activity_register_student)
                                } else {
                                    Toast.makeText(this@activity_register_student, "Error al Agregar al apoderado en Firebase.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{ Log.e("AGREGAR APODERADO", msg ?:"") }
                    Toast.makeText(this@activity_register_student, msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(this@activity_register_student, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL AGREGAR APODERADO", t.message.toString())
                }
            } )
        }
    }
}