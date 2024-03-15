package com.javierprado.jmapp.view.agregar

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.view.adapters.EstudianteAdapter
import com.javierprado.jmapp.view.menu_administrador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterApoderadoActivity : AppCompatActivity() {
    private lateinit var nombresEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var correoEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var buscarEstudiante: EditText
    private lateinit var listaEstudiantes: ListView

    private lateinit var progressBar: CircularProgressIndicator

    private lateinit var btnRegistrar: Button

    private lateinit var auth: FirebaseAuth
    private var estudiantes : Collection<Estudiante> = mutableListOf()

    private val authFunctions = AuthFunctions()
    private lateinit var api : ColegioAPI
    private lateinit var msg : String
    val TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_apoderado)

        auth = FirebaseAuth.getInstance()

        nombresEditText = findViewById(R.id.edtNombres)
        apellidosEditText = findViewById(R.id.edtApellidos)
        correoEditText = findViewById(R.id.edtEmailApoderado)
        telefonoEditText = findViewById(R.id.edtTelefono)
        direccionEditText = findViewById(R.id.edtDireccion)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        buscarEstudiante = findViewById(R.id.edtDniEstudiantes)

        progressBar = findViewById(R.id.pb_listar_estudiantes)

        listaEstudiantes = findViewById(R.id.listEstudiantes)
        msg = ""
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            retro.setBearerToken(token)
        }
        api = retro.getApi()
        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@RegisterApoderadoActivity, menu_administrador::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        buscarEstudiante.addTextChangedListener(textWatcher)

        btnRegistrar.setOnClickListener{
            val nombres = nombresEditText.text.toString().trim()
            val apellidos = apellidosEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()

            val estudiantesApoderado = HashSet<Estudiante?>()
            estudiantesApoderado.addAll(estudiantes)
            Toast.makeText(this@RegisterApoderadoActivity, estudiantesApoderado.size.toString(), Toast.LENGTH_SHORT).show()
            val apoderado = Apoderado(nombres, apellidos, correo, telefono.toInt(), direccion, estudiantesApoderado)
            api.agregarApoderado(apoderado).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) {
                        msg = response.body().toString()
                        auth.createUserWithEmailAndPassword(correo, telefono)
                            .addOnCompleteListener { task: Task<AuthResult?> ->
                                if (task.isSuccessful) {
                                    authFunctions.enviarCredenciales(correo, telefono, this@RegisterApoderadoActivity)
                                } else {
                                    Toast.makeText(this@RegisterApoderadoActivity, "Error al Agregar al apoderado en Firebase.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{ Log.e("AGREGAR APODERADO", msg ?:"") }
                    Toast.makeText(this@RegisterApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(this@RegisterApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL AGREGAR APODERADO", t.message.toString())
                }
            } )
        }
    }
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listaEstudiantes.visibility = if (estudiantes.size > 0) View.VISIBLE else View.GONE

            val text  = buscarEstudiante.text.toString().trim()
            if (text.isNotEmpty() && text.count() == 8){
                var dni = text.toInt()
                //ACTIVAR CIRCULAR
                progressBar.visibility = View.VISIBLE
                api.buscarEstudiantePorDNI(0, dni)?.enqueue(object : Callback<Estudiante>{
                    override fun onResponse(call: Call<Estudiante>, response: Response<Estudiante>) {
                        if (response.isSuccessful) {
                            val estudiante  = response.body()!!
                            val estudiantesMutable = estudiantes.toMutableList()
                            estudiantesMutable.add(estudiante)
                            estudiantes=estudiantesMutable

                            listaEstudiantes.adapter = EstudianteAdapter(this@RegisterApoderadoActivity, estudiantesMutable)
                            listaEstudiantes.visibility=View.VISIBLE
                            buscarEstudiante.setText("")
                        } else{
                            msg= "ALUMNO NO ENCONTRADO"
                            Log.e("BUSQUEDA POR DNI:", "${msg} CON EL DNI: ${dni}" )
                            Toast.makeText(this@RegisterApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Estudiante>, t: Throwable) {
                        Log.e("BUSCAR ESTUDIANTES ERROR", t.message.toString())
                    }
                })
                progressBar.visibility =View.GONE
            }else if(text.count()>9){
                buscarEstudiante.setText("")
            }
        }
    }
}