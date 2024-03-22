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

class SeleccionarAulaFragment : Fragment() {
    private var cursoId: Int = 0
    private lateinit var nivel: String
    private lateinit var docente: Docente

    private lateinit var recycler: RecyclerView
    private var aulas: MutableList<Aula> = ArrayList()

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapter : AulaAdapter
    private lateinit var msg : String

    val TOKEN = "token"
    val DIRECT = "direct"
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private val extraF = ExtraFunctions()
    private lateinit  var direct: String
    private lateinit var activity: AppCompatActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            direct = it.getString(DIRECT, "")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_seleccionar_aula, container, false)
        recycler = view.findViewById(R.id.recycler_aulas)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        adapter = AulaAdapter(ArrayList(), object : AulaClick {
            override fun onAulaClicker(aula: Aula?) {
                val fragment = extraF.obtenerFragment(direct, retro.getBearerToken(), aula!!.estudiantes as Serializable )
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_docente_main, fragment).addToBackStack(TAG).commit()
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        fun obtenerAulas(){
            val grados = if(nivel.equals("P")) 6 else 5
            val secciones = Secciones.entries.toTypedArray()
            for (i in 1 .. grados){
                for(s in secciones){
                    api.obtenerEstudiantes(cursoId, null, i, s.name).enqueue(object :
                        Callback<Collection<Estudiante>> {
                        override fun onResponse(call: Call<Collection<Estudiante>>, response: Response<Collection<Estudiante>>) {
                            msg = response.headers()["message"] ?: ""
                            if (response.isSuccessful) {
                                var aula = Aula()
                                val estudiantes = response.body()!!
                                if(estudiantes.size > 0){
                                    aula.estudiantes = estudiantes
                                    aulas.add(aula)
                                    adapter.setAulas(aulas)
                                    adapter.notifyDataSetChanged()
                                }
                            }else{
                                msg = "FAIL $msg"
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                Log.e("ERROR:", msg)
                            }
                        }
                        override fun onFailure(call: Call<Collection<Estudiante>>, t: Throwable) {
                            msg = "Error en la API: ${t.message}"
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            Log.e("OBTENER ESTUDIANTES", t.message.toString())
                        }
                    } )
                }
            }
        }

        api.obtenerSesion(RoleType.DOC.name).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                msg = "Usuario no encontrado."
                if (response.isSuccessful) {
                    docente = response.body()!!.docente
                    nivel = docente.curso!!.nivelEducativo.toString()
                    cursoId = docente.curso!!.cursoId
                    obtenerAulas()
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
    companion object {
        @JvmStatic
        fun newInstance(token: String, direct: String) =
            SeleccionarAulaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(DIRECT, direct)
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