package com.javierprado.jmapp.view.activities.agregar

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
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.view.adapters.EstudianteAdapter
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
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
    private lateinit var firestore : FirebaseFirestore
    private var estudiantes : List<Estudiante> = mutableListOf()

    private val authFunctions = AuthFunctions()
    private lateinit var api : ColegioAPI
    private lateinit var msg : String
    val TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_apoderado)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
//            val intent = Intent(this@RegisterApoderadoActivity, MenuAdministradorActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
            finish()
        }

        buscarEstudiante.addTextChangedListener(textWatcher)

        btnRegistrar.setOnClickListener{
            val nombres = nombresEditText.text.toString().trim()
            val apellidos = apellidosEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()

            val estudiantesApoderado: List<String> = estudiantes.map { e->e.id }
            if(telefono.isNotEmpty()){
                val apoderado = Apoderado(nombres, apellidos, correo, telefono.toInt(), direccion, estudiantesApoderado)
                Log.e("APODERADO", apoderado.toString())
                api.guardarApoderado(apoderado, "null").enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val id = msg
                            auth.createUserWithEmailAndPassword(correo, telefono)
                                .addOnCompleteListener { task: Task<AuthResult?> ->
                                    if (task.isSuccessful) {
                                        // GUARDAR USUARIO EN FIRESTORE (USERS)
                                        val user = auth.currentUser!!
                                        val dataHashMap = hashMapOf("userid" to user.uid, "info" to "$nombres $apellidos", "correo" to correo, "estado" to "default", "tipo" to "APOD",
                                            "tipoid" to id, "token" to "" )
                                        firestore.collection("Users").document(user.uid).set(dataHashMap)
                                        authFunctions.enviarCredenciales(correo, telefono, this@RegisterApoderadoActivity)
                                        val intent = Intent(this@RegisterApoderadoActivity, MenuAdministradorActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this@RegisterApoderadoActivity, "Error al Agregar en Firebase.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else{
                            Log.e("AGREGAR APODERADO", msg)
                            Toast.makeText(this@RegisterApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        msg = "Error en la API: ${t.message}"
                        Toast.makeText(this@RegisterApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("ERROR AL AGREGAR APODERADO", t.message.toString())
                    }
                } )
            }else{
                Toast.makeText(this@RegisterApoderadoActivity, "Completar el campo de teléfono.", Toast.LENGTH_SHORT).show()
            }


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
                api.buscarEstudiante("null", dni).enqueue(object : Callback<Estudiante>{
                    override fun onResponse(call: Call<Estudiante>, response: Response<Estudiante>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val estudiante  = response.body()!!
                            val estudiantesMutable = estudiantes.toMutableList()
                            estudiantesMutable.add(estudiante)
                            estudiantes=estudiantesMutable

                            listaEstudiantes.adapter = EstudianteAdapter(this@RegisterApoderadoActivity, estudiantesMutable)
                            listaEstudiantes.visibility=View.VISIBLE
                            buscarEstudiante.setText("")
                        } else{
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