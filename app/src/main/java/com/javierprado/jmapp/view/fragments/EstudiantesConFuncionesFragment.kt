package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.adapters.EstudianteFuncsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class EstudiantesConFuncionesFragment : Fragment() {
    private lateinit var recycler: RecyclerView

    private var adapter : EstudianteFuncsAdapter = EstudianteFuncsAdapter()
    private lateinit var estudiantes: List<String>

    private var docenteId = ""; private var cursoId = ""
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(MenuAdministradorActivity().TOKEN, ""))
            estudiantes = it.getStringArrayList(MenuDocenteActivity().ESTUDIANTES)?.toList() ?: ArrayList()
            docenteId = it.getString(MenuAdministradorActivity().USUARIOID, "")
            cursoId = it.getString(MenuDocenteActivity().CURSOID, "")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_estudiantes_con_funciones, container, false)
        recycler = view.findViewById(R.id.recycler_estudiantes_f)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()
        api.listarEstudiantes("null", estudiantes).enqueue(object : Callback<List<Estudiante>> {
            override fun onResponse(call: Call<List<Estudiante>>, response: Response<List<Estudiante>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val estudiantesL = response.body()!!
                    adapter = EstudianteFuncsAdapter(estudiantesL, retro.getBearerToken(), cursoId, activity)
                    recycler.layoutManager = LinearLayoutManager(context)
                    recycler.adapter = adapter
                } else{
                    Log.e("LISTAR ESTUDIANTES", msg)
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStackImmediate()
                }

            }
            override fun onFailure(call: Call<List<Estudiante>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                Log.e("LISTAR ESTUDIANTES", t.message.toString())
            }
        } )
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiantes: Serializable, docenteId: String, cursoId: String) =
            EstudiantesConFuncionesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(MenuDocenteActivity().ESTUDIANTES, estudiantes)
                    putSerializable(MenuAdministradorActivity().USUARIOID, docenteId)
                    putSerializable(MenuDocenteActivity().CURSOID, cursoId)
                }
            }
    }
}