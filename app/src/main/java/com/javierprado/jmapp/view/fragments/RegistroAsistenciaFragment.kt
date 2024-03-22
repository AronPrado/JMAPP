package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.adapters.AsistenciaAdapter
import com.javierprado.jmapp.view.clicks.AsistenciaClick
import com.javierprado.jmapp.view.clicks.AulaClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.time.LocalDate

class RegistroAsistenciaFragment : Fragment() {
//    private lateinit var nivelSpinner: Spinner
//    private lateinit var gradoSpinner: Spinner
//    private lateinit var selectSpinner: Spinner
    private lateinit var recycler: RecyclerView
    private lateinit var btnRegistrar: Button
    private lateinit var fechaAsistencia: TextView

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapter : AsistenciaAdapter
    private var estudiantes: Collection<Estudiante> = ArrayList()
    private var asistencias: List<Asistencia> = ArrayList()
    private lateinit var msg : String

    val ESTUDIANTES = "estudiantes"
    val TOKEN = "token"
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
        val view = inflater.inflate(R.layout.fragment_registro_asistencia, container, false)
        recycler = view.findViewById(R.id.recycler_asistencias)
        fechaAsistencia = view.findViewById(R.id.fecha_asistencia)
        btnRegistrar = view.findViewById(R.id.btn_registrar_asistencia)
        progressC = view.findViewById(R.id.pb_registrar_asistencias)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        val fecha = LocalDate.now().toString()
        fechaAsistencia.text = fecha
        adapter = AsistenciaAdapter(ArrayList(), object: AsistenciaClick {
            override fun onAsistenciaClicker(asistencia: Asistencia?) {
                Toast.makeText(activity, "CAMBIAR ESTADO DE ASISTENCIA", Toast.LENGTH_SHORT).show()
//                val fragment = extraF.obtenerFragment(direct)
//                                .newInstance(horario!!, retro.getBearerToken())
//                activity.supportFragmentManager.beginTransaction()
//                    .replace(R.id.fcv_docente_main, fragment).addToBackStack(TAG).commit()
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        progressC.visibility = View.VISIBLE

        api.obtenerAsistencias(fecha, estudiantes).enqueue(object : Callback<Collection<Asistencia>> {
            override fun onResponse(call: Call<Collection<Asistencia>>, response: Response<Collection<Asistencia>>) {
                if (response.isSuccessful) {
                    msg = "Las asistencias no están para todos los estudiantes."
                    val asistenciasR = response.body()!!.toList()
                    if(estudiantes.size == asistenciasR.size){
                        msg = "Asistencias obtenidas correctamente"
                        adapter.setAsistencias(asistenciasR)
                        adapter.notifyDataSetChanged()
                        asistencias = asistenciasR
                        progressC.visibility = View.GONE
                    }else{
                        Log.e("TOTALES", "Estudiantes: ${estudiantes.size} - Asistencias: ${asistencias.size}")
                    }
                }
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<Collection<Asistencia>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                Log.e("OBTENER ASISTENCIAS", t.message.toString())
            }
        } )
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiantes: Serializable) =
            RegistroAsistenciaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(ESTUDIANTES, estudiantes)
                }
            }
    }
}