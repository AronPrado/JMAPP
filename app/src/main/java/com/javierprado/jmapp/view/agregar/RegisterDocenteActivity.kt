package com.javierprado.jmapp.view.agregar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AuthFunctions
import com.javierprado.jmapp.view.adapters.CursoAdapter
import com.javierprado.jmapp.view.menus.menu_administrador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterDocenteActivity : AppCompatActivity() {
    private lateinit var nombresEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var generoEditText: Spinner
    private lateinit var correoEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var listaNivelEducativo: Spinner
    private lateinit var listaCursos: ListView
    
    private lateinit var btnRegistrar: Button

    private lateinit var cursos : Collection<Curso>

    private val authFunctions = AuthFunctions()
    private lateinit var auth: FirebaseAuth
    private lateinit var api : ColegioAPI
    private lateinit var msg : String

    private var cursoDocente : Curso? = null
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_docente)
        
        auth = FirebaseAuth.getInstance()

        nombresEditText = findViewById(R.id.edtNombres)
        apellidosEditText = findViewById(R.id.edtApellidos)
        fechaNacimientoEditText = findViewById(R.id.edtFechaNacDocente)
        generoEditText = findViewById(R.id.s_genero__docente)
        correoEditText = findViewById(R.id.edtEmailDocente)
        telefonoEditText = findViewById(R.id.edtTelefono)
        direccionEditText = findViewById(R.id.edtDireccion)
        listaNivelEducativo = findViewById(R.id.s_nivel_educativo_curso__docente)
        listaCursos = findViewById(R.id.lv_lista_cursos)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        msg = ""
        // API
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
            val intent = Intent(this@RegisterDocenteActivity, menu_administrador::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        listaNivelEducativo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val nivel = listaNivelEducativo.selectedItem.toString()[0].toString()

                /*val progressBarListar: CircularProgressIndicator = findViewById(R.id.pb_listar_cursos)
                progressBarListar.visibility = View.VISIBLE
                listaCursos.visibility = View.GONE*/

                api.obtenerCursos(null, nivel)?.enqueue(object : Callback<Collection<Curso>> {
                    override fun onResponse(call: Call<Collection<Curso>>, response: Response<Collection<Curso>>) {
                        if (response.isSuccessful) {
                            cursos = response.body()!!
                            listaCursos.adapter = CursoAdapter(this@RegisterDocenteActivity, cursos as MutableList<Curso>)

                            listaCursos.visibility = if (!cursos.isEmpty()) View.VISIBLE else View.GONE

                            if (cursos.isEmpty()){
                                msg="No se encontraron cursos en este nivel educativo."
                                Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                            //progressBarListar.visibility= View.GONE
                        }
                    }
                    override fun onFailure(call: Call<Collection<Curso>>, t: Throwable) {
                        msg = "Error en la API: ${t.message}"
                        Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("LISTAR CURSOS", t.message.toString())
                        //progressBarListar.visibility= View.GONE
                    }
                } )
            }
        }
        
        btnRegistrar.setOnClickListener{
            val nombres = nombresEditText.text.toString().trim()
            val apellidos = apellidosEditText.text.toString().trim()
            val fechaNac = fechaNacimientoEditText.text.toString().trim()
            val genero = generoEditText.selectedItem.toString()
            val correo = correoEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()
            val curso = cursoDocente

            val docente = Docente(nombres, apellidos, fechaNac, genero, correo, telefono.toInt(), direccion,  curso)
            api.agregarDocente(docente).enqueue(object : Callback<Docente> {
                override fun onResponse(call: Call<Docente>, response: Response<Docente>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) {
                        val docenteRegistrado = response.body()
                        msg = "Docente nuevo ID: ${docenteRegistrado?.docenteId}"
                        auth.createUserWithEmailAndPassword(correo, telefono)
                            .addOnCompleteListener { task: Task<AuthResult?> ->
                                if (task.isSuccessful) {
                                    authFunctions.enviarCredenciales(correo, telefono, this@RegisterDocenteActivity)
                                } else {
                                    Toast.makeText(this@RegisterDocenteActivity, "Error al Agregar al docente en Firebase.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{ Log.e("AGREGAR DOCENTE", msg) }
                    Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Docente>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ERROR AL AGREGAR DOCENTE", t.message.toString())
                }
            } )
        }
    }

    fun onCursoSelected(curso: Curso) {
        cursoDocente = curso
    }
}