package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.adapters.AulaAdapter
import com.javierprado.jmapp.view.adapters.EstudianteAdapter
import com.javierprado.jmapp.view.adapters.EstudianteSeleccionAdapter
import com.javierprado.jmapp.view.adapters.HijoApoderadoAdapter
import com.javierprado.jmapp.view.adapters.onEstudianteApoClickListener
import com.javierprado.jmapp.view.clicks.AulaClick
import com.javierprado.jmapp.view.clicks.EstudianteFClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class EstudiantesHFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private var estudiantes: List<Estudiante> = ArrayList()
    private lateinit var adapter: EstudianteSeleccionAdapter

    private lateinit var progressC: CircularProgressIndicator

    private var apoderadoId = "" ; private var tokenApod = "" ; private var direct = ""
    private val TAG: String  = tag.toString()
    private val extraF = ExtraFunctions()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    override fun onAttach(context: Context) {
        super.onAttach(context) ; activity = context as ControlEstudianteActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apoderadoId = it.getString(MenuAdministradorActivity().USUARIOID, "")
            direct = it.getString(ControlEstudianteActivity().DIRECT, "")
            tokenApod = it.getString(MenuAdministradorActivity().TOKEN, "")
            retro.setBearerToken(tokenApod)
            estudiantes = it.getSerializable(MenuApoderadoActivity().HIJOS) as List<Estudiante>
            Log.e("HIJOS", estudiantes.size.toString())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estudiantes_h, container, false)
        recycler = view.findViewById(R.id.recycler_estudiantes_h)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        adapter = EstudianteSeleccionAdapter(estudiantes, object : EstudianteFClick {
            override fun onEstudianteFClicker(estudiante: Estudiante?) {
//                Log.e("DIRECT", direct)
                val fragment = extraF.obtenerFragment(direct, retro.getBearerToken(), estudiantes as Serializable,
                    apoderadoId, estudiante?.aulaId!! +"-"+estudiante.id)
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_apoderado_main, fragment).addToBackStack(TAG).commit()
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.setEstudiantes(estudiantes)

    }

    companion object {
        @JvmStatic
        fun newInstance(token: String, direct: String, apoderadoId: String, hijos: Serializable) =
            EstudiantesHFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(MenuAdministradorActivity().USUARIOID, apoderadoId)
                    putSerializable(ControlEstudianteActivity().DIRECT, direct)
                    putSerializable(MenuApoderadoActivity().HIJOS, hijos)
                }
            }
    }
    override fun onDestroy() {
        activity.finish()
//        val intent = Intent(context, MenuApoderadoActivity::class.java)
//        intent.putExtra(ControlEstudianteActivity().DIRECT, NavigationWindows.REUNIONES.name)
//        intent.putExtra(MenuAdministradorActivity().USUARIOID, apoderadoId)
//        intent.putExtra(MenuAdministradorActivity().TOKEN, tokenApod)
//        intent.putExtra(MenuApoderadoActivity().HIJOS, estudiantes as Serializable)
//        startActivity(intent)
        super.onDestroy()
    }
}