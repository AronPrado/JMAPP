package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.content.Intent
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
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.Secciones
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.adapters.AulaAdapter
import com.javierprado.jmapp.view.adapters.EstudianteFuncsAdapter
import com.javierprado.jmapp.view.clicks.AulaClick
import com.javierprado.jmapp.view.clicks.EstudianteFClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.Optional
import kotlin.properties.Delegates

class EstudiantesConFuncionesFragment : Fragment() {
    private lateinit var recycler: RecyclerView

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapter : EstudianteFuncsAdapter
    private var estudiantes: Collection<Estudiante> = ArrayList()
    private lateinit var msg : String

    val ESTUDIANTES = "estudiantes"
    val TOKEN = "token"
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
        val view = inflater.inflate(R.layout.fragment_estudiantes_con_funciones, container, false)
        recycler = view.findViewById(R.id.recycler_estudiantes_f)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EstudianteFuncsAdapter(estudiantes.toList(), retro.getBearerToken(), activity)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiantes: Serializable) =
            EstudiantesConFuncionesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(ESTUDIANTES, estudiantes)
                }
            }
    }

    override fun onDestroy() {
        val intent = Intent(context, MenuDocenteActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        super.onDestroy()
    }
}