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
import com.javierprado.jmapp.clases.NewsAdapter
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.adapters.CursoAdapter
import com.javierprado.jmapp.view.adapters.EstudianteAdapter
import com.javierprado.jmapp.view.menu_administrador
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
    
    private lateinit var auth: FirebaseAuth
    private lateinit var api : ColegioAPI
    private lateinit var msg : String
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_docente)
        
        auth = FirebaseAuth.getInstance()

        nombresEditText = findViewById(R.id.edtNombres)
        apellidosEditText = findViewById(R.id.edtApellidos)
        fechaNacimientoEditText = findViewById(R.id.edtFechaNacDocente)
        generoEditText = findViewById(R.id.s_genero__ocente)
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
            Toast.makeText(this@RegisterDocenteActivity, "BEARER ESTABLECIDO", Toast.LENGTH_SHORT).show()
            Log.e("BEARER",token)

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
                val nivel = listaNivelEducativo.selectedItem.toString()[0]
                api.obtenerCursos(null, nivel.toString())?.enqueue(object : Callback<Collection<Curso>> {
                    override fun onResponse(call: Call<Collection<Curso>>, response: Response<Collection<Curso>>) {
                        listaCursos.visibility = if (response.isSuccessful) View.VISIBLE else View.GONE
                        if (response.isSuccessful) {
                            cursos = response.body()!!
                            if (cursos.isEmpty()){
                                listaCursos.adapter = CursoAdapter(this@RegisterDocenteActivity, cursos as MutableList<Curso>)
                            } else {
                                msg="No se encontraron cursos en este nivel educativo."
                                Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            msg = response.errorBody()?.string().toString()
                            Toast.makeText(this@RegisterDocenteActivity, "NOT RESPONSE", Toast.LENGTH_SHORT).show()
                            Log.e("NOT RESPONSE", msg)
                        }
                    }
                    override fun onFailure(call: Call<Collection<Curso>>, t: Throwable) {
                        msg = "Error en la API: ${t.message}"
                        Toast.makeText(this@RegisterDocenteActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("ERROR AL AGREGAR APODERADO", t.message.toString())
                    }
                } )
            }
        }
    }
}