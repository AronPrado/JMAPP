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
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.notificaciones.NotificacionesJMA
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.adapters.AsistenciaAdapter
import com.javierprado.jmapp.view.clicks.AsistenciaClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.time.LocalDate

class RegistroAsistenciaFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private lateinit var btnRegistrar: Button
    private lateinit var fechaAsistencia: TextView

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapter : AsistenciaAdapter
    private lateinit var estudiantes: List<String>
    private var asistencias: MutableList<Asistencia> = ArrayList()

    val ESTUDIANTES = "estudiantes"
    val TOKEN = "token"
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private val notiFuncs = NotificacionesJMA()
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            estudiantes = it.getStringArrayList(ESTUDIANTES)?.toList() ?: ArrayList()
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
//        estudiantes, fecha, cursoId, docenteId
        api.listarAsistencias(estudiantes, fecha, "", "" ).enqueue(object : Callback<List<Asistencia>> {
            override fun onResponse(call: Call<List<Asistencia>>, response: Response<List<Asistencia>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    asistencias = response.body()!!.toList() as MutableList<Asistencia>
                    adapter.setAsistencias(asistencias)
                    progressC.visibility = View.GONE
                }else{ Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show() }
            }
            override fun onFailure(call: Call<List<Asistencia>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                Log.e("OBTENER ASISTENCIAS", t.message.toString())
            }
        } )

        btnRegistrar.setOnClickListener {
            if (asistencias.isNotEmpty()){
                api.editarAsistencias(asistencias).enqueue(object : Callback<List<String>> {
                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val emails = response.body()!!
                            notiFuncs.notificarFalta(viewLifecycleOwner, emails, "Matemática", activity)
                        }else{
                            Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                        msg = "Error en la API: ${t.message}"
                        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                        Log.e("EDITAR ASISTENCIAS", t.message.toString())
                    }
                } )
            }
        }
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