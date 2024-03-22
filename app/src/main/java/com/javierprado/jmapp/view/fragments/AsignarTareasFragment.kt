package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Tarea
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class AsignarTareasFragment : Fragment() {

    private lateinit var btnAsignar: Button
    private lateinit var descripcionTarea: EditText
    private lateinit var fechaEntrega: EditText

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var curso : Curso
    private lateinit var estudiantes : Collection<Estudiante>
    private lateinit var msg : String

    val TOKEN = "token"
    val ESTUDIANTES = "estudiantes"
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            estudiantes = it.getSerializable(ESTUDIANTES) as Serializable as Collection<Estudiante>
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_asignar_tareas, container, false)
        btnAsignar = view.findViewById(R.id.btn_asignar_tarea)
        descripcionTarea = view.findViewById(R.id.txt_descripcion_tarea)
        fechaEntrega = view.findViewById(R.id.txt_fechaentrega_tarea)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        fun obtenerCurso(){
            api.obtenerSesion(RoleType.DOC.name).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    msg = "Usuario no encontrado."
                    if (response.isSuccessful) {
                        curso = response.body()!!.docente.curso!!
                        Log.e("CURSO OBTENIDO", curso.nombre!!)
                    } else{
                        Log.e("NR SESSION", msg)
                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("BUSCAR SESSION", t.message.toString())
                }
            } )
        }

        obtenerCurso()
        btnAsignar.setOnClickListener{
            val descripcion = descripcionTarea.text.toString()
            val fecha = fechaEntrega.text.toString()
            val estado = "ASIGNADA"
            val cursos = HashSet<Curso>() ; cursos.add(curso)

            val tarea = Tarea(descripcion, estado, fecha, cursos)
//            val estudiantes: Collection<Estudiante> = ArrayList()
            api.agregarTareas(tarea).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) {
                        msg = "Tarea creada"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("AGREGAR TAREA", t.message.toString())
                }
            } )
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiantes: Serializable) =
            AsignarTareasFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(ESTUDIANTES, estudiantes)
                }
            }
    }
}