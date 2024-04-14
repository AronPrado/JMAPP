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
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.adapters.AulaAdapter
import com.javierprado.jmapp.view.clicks.AulaClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class SeleccionarAulaFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private var aulas: MutableList<Aula> = ArrayList()

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapter : AulaAdapter
    private lateinit var msg : String

    private var docenteId = "" ; private var tokenDoc = ""; private var cursoId = ""
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
            cursoId = it.getString(MenuDocenteActivity().CURSOID, "")
            direct = it.getString(ControlSeleccionActivity().DIRECT, "")
            docenteId = it.getString(MenuAdministradorActivity().USUARIOID, "")
            tokenDoc = it.getString(MenuAdministradorActivity().TOKEN, "")
            retro.setBearerToken(tokenDoc)
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
                val fragment = extraF.obtenerFragment(direct, retro.getBearerToken(), aula!!.estudiantes as Serializable,
                    docenteId, cursoId)
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_docente_main, fragment).addToBackStack(TAG).commit()
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        api.listarAulas(docenteId, null, null, null).enqueue(object : Callback<List<Aula>> {
            override fun onResponse(call: Call<List<Aula>>, response: Response<List<Aula>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    aulas = response.body()!! as MutableList<Aula>
                    adapter.setAulas(aulas)
                }else{
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("OBTENER AULAS:", msg)
                }
            }
            override fun onFailure(call: Call<List<Aula>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                Log.e("OBTENER AULAS", t.message.toString())
            }
        } )
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, direct: String, docenteId: String, cursoId: String) =
            SeleccionarAulaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(MenuAdministradorActivity().USUARIOID, docenteId)
                    putSerializable(MenuDocenteActivity().CURSOID, cursoId)
                    putSerializable(ControlSeleccionActivity().DIRECT, direct)
                }
            }
    }

    override fun onDestroy() {
        activity.supportFragmentManager.popBackStackImmediate()
//        val intent = Intent(context, MenuDocenteActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        intent.putExtra(ControlSeleccionActivity().TOKEN, tokenDoc)
//        intent.putExtra(SeleccionarAulaFragment().DIRECT, direct)
//        intent.putExtra(MenuDocenteActivity().USUARIOID, docenteId)
//        startActivity(intent)
        super.onDestroy()
    }
}