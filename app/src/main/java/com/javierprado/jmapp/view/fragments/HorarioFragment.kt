package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.adapters.HorarioAdapter
import com.javierprado.jmapp.view.clicks.HorarioClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.time.LocalDate
import java.util.Calendar

class HorarioFragment : Fragment() {
    private val dias = listOf("Lunes","Martes","Miércoles","Jueves","Viernes")

    private lateinit var nivelA: String
    private var gradoA: Int = 0 ; private var estVer: Int = 0
    private lateinit var seleccionA: String

    private lateinit var recyclerViews: MutableList<RecyclerView>
    private lateinit var textViewsDias: MutableList<TextView>

    private var fechaLunes = LocalDate.now()

    private lateinit var estudiantesNombres : List<Estudiante>

    private lateinit var nivelSpinner: Spinner
    private lateinit var gradoSpinner: Spinner
    private lateinit var selectSpinner: Spinner
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var txtMes: TextView

    private lateinit var progressC: CircularProgressIndicator
    private lateinit var adapters : MutableList<HorarioAdapter>

    var PARAADMIN = "forAdmin"
    var paraAdmin = false ; var usuarioId = "";
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlHorarioActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(MenuAdministradorActivity().TOKEN, ""))
            usuarioId = it.getString(MenuAdministradorActivity().USUARIOID, usuarioId)
            paraAdmin = it.getBoolean(PARAADMIN, false)
            estudiantesNombres = it.getSerializable(ControlHorarioActivity().ESTUDIANTES) as List<Estudiante>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_horario, container, false)

        nivelSpinner = view.findViewById(R.id.s_nivel_educativo_horario)
        gradoSpinner = view.findViewById(R.id.s_grado_horario)
        selectSpinner = view.findViewById(R.id.s_select_horario)
        btnNext = view.findViewById(R.id.btn_next_horario)
        btnBack = view.findViewById(R.id.btn_back_horario)
        txtMes = view.findViewById(R.id.txt_mes_horario)

        progressC = view.findViewById(R.id.pb_fragment_horario)

        if(!paraAdmin){
            nivelSpinner.visibility = View.GONE ; gradoSpinner.visibility = View.GONE

            val nombres = estudiantesNombres.map { e-> e.nombres + " " + e.apellidos }
            val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, nombres as ArrayList<String>)
            selectSpinner.adapter=adapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        obtenerFechas()
        establecerAdapters(view, api)

        btnBack.setOnClickListener { moverEnSemanas(api, false) }
        btnNext.setOnClickListener { moverEnSemanas(api, true) }

        selectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filtrarHorario(api)
            }
        }
    }
    private fun filtrarHorario(api: ColegioAPI) {
        seleccionA = selectSpinner.selectedItem.toString()
        if (paraAdmin){
            nivelA = nivelSpinner.selectedItem.toString()[0].toString()
            gradoA = gradoSpinner.selectedItem.toString()[0].toString().toInt()
        } else{
            val index = (selectSpinner.adapter as ArrayAdapter<String>).getPosition(seleccionA)
            val estudianteSeleccionado = estudiantesNombres[index]
            nivelA = estudianteSeleccionado.nivelEducativo
            gradoA = estudianteSeleccionado.grado
            seleccionA = estudianteSeleccionado.seccion
        }

        progressC.visibility = View.VISIBLE
        api.listarHorarios(gradoA, nivelA, seleccionA, fechaLunes.toString()).enqueue(object : Callback<List<List<Horario>>> {
            override fun onResponse(call: Call<List<List<Horario>>>, response: Response<List<List<Horario>>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val horarios = response.body()!!
                    for((i, hh) in horarios.withIndex()){ adapters[i].setHorarios(hh) ; }
                }else{
                    estVer++
                    if(estVer == estudiantesNombres.size){
                        activity.supportFragmentManager.popBackStackImmediate()
                    }
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("LISTAR HORARIOS", msg)
                }
                progressC.visibility = View.GONE
            }
            override fun onFailure(call: Call<List<List<Horario>>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                Log.e("LISTAR HORARIOS", t.message.toString())
            }
        } )
    }
    private fun establecerAdapters(view: View, api: ColegioAPI){
        recyclerViews = ArrayList() ; textViewsDias = ArrayList() ; adapters = ArrayList()
        val idsR = listOf(R.id.recycler_horarios_lun, R.id.recycler_horarios_mar, R.id.recycler_horarios_mie, R.id.recycler_horarios_jue, R.id.recycler_horarios_vie)
        val idsT = listOf(R.id.dia_lunes, R.id.dia_martes, R.id.dia_miercoles, R.id.dia_jueves, R.id.dia_viernes)
        for (i in idsR.indices){
            val recycler : RecyclerView = view.findViewById(idsR[i])
            val diaText : TextView = view.findViewById(idsT[i])

            val adapter = HorarioAdapter(ArrayList(), api, object : HorarioClick {
                override fun onHorarioClicker(horario: Horario?) {
                    if(paraAdmin){
                        val fragment: Fragment = EditarHorarioFragment.newInstance(horario!!, retro.getBearerToken())
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fcv_horario_main, fragment)
                            .addToBackStack(TAG)
                            .commit()
                    }
                }
            })
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            recyclerViews.add(recycler) ; textViewsDias.add(diaText) ; adapters.add(adapter)
        }
    }
    private fun obtenerFechas(){
        val diferenciaDias = fechaLunes.dayOfWeek.value - Calendar.MONDAY+1
        fechaLunes = fechaLunes.minusDays(diferenciaDias.toLong())
        var nombreMesActual = fechaLunes.month.name
        txtMes.text = nombreMesActual

        Log.e("FECHA DEL LUNES", fechaLunes.toString())
    }
    private fun moverEnSemanas(api: ColegioAPI, siguiente: Boolean){
        fechaLunes = if (siguiente) fechaLunes.plusWeeks(1.toLong()) else fechaLunes.minusWeeks(1.toLong())
        obtenerFechas() ; filtrarHorario(api)
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String, usuarioId: String,paraAdmin: Boolean, estudiantes: List<Estudiante>) =
            HorarioFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(MenuAdministradorActivity().USUARIOID, usuarioId)
                    putSerializable(PARAADMIN, paraAdmin)
                    putSerializable(ControlHorarioActivity().ESTUDIANTES, estudiantes as Serializable)
                }
            }
    }
    override fun onDestroy() {
        val intent = ControlHorarioActivity().getActivityRol(context, paraAdmin)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(MenuAdministradorActivity().USUARIOID, usuarioId)
        intent.putExtra(MenuAdministradorActivity().TOKEN, retro.getBearerToken())
        startActivity(intent) ; activity.finish()
        super.onDestroy()
    }
}