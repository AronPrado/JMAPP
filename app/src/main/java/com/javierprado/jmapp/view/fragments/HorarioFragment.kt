package com.javierprado.jmapp.view.fragments

import android.annotation.SuppressLint
import android.content.Context
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
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
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
    private var gradoA: Int = 0
    private lateinit var seleccionA: String

    private lateinit var recyclerViews: MutableList<RecyclerView>
    private lateinit var textViewsDias: MutableList<TextView>

    private var fechaMostrar = LocalDate.now()
    private lateinit var rangoFechas: MutableList<LocalDate>

    private var estudiantes : List<Estudiante>? = ArrayList()

    private lateinit var nivelSpinner: Spinner
    private lateinit var gradoSpinner: Spinner
    private lateinit var selectSpinner: Spinner
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private lateinit var txtMes: TextView

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var adapters : MutableList<HorarioAdapter>
    private lateinit var msg : String

    val TOKEN = "token"
    var PARAADMIN = "forAdmin"
    var ESTUDIANTES = "estudiantes"
    var paraAdmin = false
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlHorarioActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            paraAdmin = it.getBoolean(PARAADMIN, false)
            estudiantes = it.getSerializable(ESTUDIANTES) as List<Estudiante>
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
            nivelSpinner.visibility = View.GONE
            gradoSpinner.visibility = View.GONE

            val nombres = estudiantes?.map { e-> e.nombres!! + " " + e.apellidos!! }
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
            val estudianteSeleccionado = estudiantes?.get(index)!!
            nivelA = estudianteSeleccionado.nivelEducativo!!
            gradoA = estudianteSeleccionado.grado!!
            seleccionA = estudianteSeleccionado.seccion!!
        }

        var cursos : List<Curso>

        progressC.visibility = View.VISIBLE
        api.obtenerEstudiantes(null, nivelA, gradoA, seleccionA).enqueue(object :
            Callback<Collection<Estudiante>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Collection<Estudiante>>, response: Response<Collection<Estudiante>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val estudiantes = response.body()!!
                    if(!estudiantes.isEmpty()){
                        val estudiante = (estudiantes as MutableList)[0].estudianteId
                        api.obtenerCursos(estudiante, null).enqueue(object : Callback<Collection<Curso>> {
                            override fun onResponse(call: Call<Collection<Curso>>, response: Response<Collection<Curso>>) {
                                if (response.isSuccessful) {
                                    for (i in 0..<rangoFechas.size){
                                        val fecha = rangoFechas[i].toString()
                                        val diaNum = dias[i] + "\n" + fecha.substring(8)
                                        textViewsDias[i].text = diaNum
                                        val idsCursoDia = obtenerCursosXDia(i, response.body()!!.toList())
                                        api.obtenerHorarios(fecha, idsCursoDia).enqueue(object :
                                            Callback<Collection<Horario>> {
                                            override fun onResponse(call: Call<Collection<Horario>>, response: Response<Collection<Horario>>) {
                                                if (response.isSuccessful) {
                                                    val horarios = response.body()!!.toList()
                                                    adapters[i].setHorarios(horarios)
                                                    adapters[i].notifyDataSetChanged()
                                                }
                                            }
                                            override fun onFailure(call: Call<Collection<Horario>>, t: Throwable) {
                                                msg = "Error en la API: ${t.message}"
                                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                                Log.e("LISTAR HORARIOS", t.message.toString())
                                            }
                                        } )
                                    }
                                    progressC.visibility = View.GONE
                                }
                            }
                            override fun onFailure(call: Call<Collection<Curso>>, t: Throwable) {
                                msg = "Error en la API: ${t.message}"
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                Log.e("LISTAR CURSOS", t.message.toString())
                            }
                        } )

                    }
                }else{
                    msg = "FAIL $msg"
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
        rangoFechas = ArrayList()
        val diferenciaDias = fechaMostrar.dayOfWeek.value - Calendar.MONDAY+1
        val fechaLunes = fechaMostrar.minusDays(diferenciaDias.toLong())
        Log.e("FECHA DEL LUNES", fechaLunes.toString())
        var fechaActual = fechaLunes
        var mesActual = fechaActual.month.value
        var nombreMesActual = fechaActual.month.name
        for (i in 0..4) {
            if(fechaActual.month.value != mesActual ){
                nombreMesActual+= " - ${fechaActual.month.name}"
            }
            rangoFechas.add(fechaActual)
            fechaActual = fechaLunes.plusDays(i+1.toLong())
        }
        txtMes.text = nombreMesActual
    }
    private fun moverEnSemanas(api: ColegioAPI, siguiente: Boolean){
        fechaMostrar = if (siguiente) fechaMostrar.plusWeeks(1.toLong()) else fechaMostrar.minusWeeks(1.toLong())
        obtenerFechas()
        filtrarHorario(api)
    }
    private fun obtenerCursosXDia(dia: Int, cursos: List<Curso>): List<Int>{
        return cursos.filter { it.dia == dias[dia] }.map { it.cursoId }
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, paraAdmin: Boolean, estudiantes: List<Estudiante>) =
            HorarioFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(PARAADMIN, paraAdmin)
                    putSerializable(ESTUDIANTES, estudiantes as Serializable)
                }
            }
    }
}