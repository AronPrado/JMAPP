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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.mvvm.UsersRepo
import com.javierprado.jmapp.notificaciones.NotificacionesJMA
import com.javierprado.jmapp.notificaciones.entities.NotificationData
import com.javierprado.jmapp.notificaciones.entities.PushNotification
import com.javierprado.jmapp.notificaciones.entities.Token
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
    private var estudiantes: Collection<Estudiante> = ArrayList()
    private var asistencias: MutableList<Asistencia> = ArrayList()
    private lateinit var msg : String

    val ESTUDIANTES = "estudiantes"
    val TOKEN = "token"
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private val notiFuncs = NotificacionesJMA()
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
                    asistencias = response.body()!!.toList() as MutableList<Asistencia>
                    if(estudiantes.size == asistencias.size){
                        msg = "Asistencias obtenidas correctamente"
                        adapter.setAsistencias(asistencias)
                        progressC.visibility = View.GONE
                    }else{
                        Log.e("TOTALES", "Estudiantes: ${estudiantes.size} - Asistencias: ${asistencias.size}")
                        Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<Collection<Asistencia>>, t: Throwable) {
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