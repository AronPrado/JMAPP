package com.javierprado.jmapp.view.agregar

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.adapters.HorarioAdapter
import com.javierprado.jmapp.view.menu_administrador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Calendar

class ControlHorarioActivity : AppCompatActivity() {

    private val dias = listOf("Lunes","Martes","Miércoles","Jueves","Viernes")

    private var fechaMostrar = LocalDate.now()
    private lateinit var rangoFechas: MutableList<LocalDate>

    private lateinit var recyclerViews: MutableList<RecyclerView>
    private lateinit var textViewsDias: MutableList<TextView>
    private lateinit var nivelSpinner: Spinner
    private lateinit var gradoSpinner: Spinner
    private lateinit var seccionSpinner: Spinner
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    private lateinit var progressBar: CircularProgressIndicator

    private lateinit var adapters : MutableList<HorarioAdapter>
    private lateinit var api : ColegioAPI
    private lateinit var msg : String
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_horario)

        nivelSpinner = findViewById(R.id.s_nivel_educativo_horario)
        gradoSpinner = findViewById(R.id.s_grado_horario)
        seccionSpinner = findViewById(R.id.s_seccion_horario)
        btnNext = findViewById(R.id.btn_next_horario)
        btnBack = findViewById(R.id.btn_back_horario)

        progressBar = findViewById(R.id.pb_control_horario)

        msg = ""
        // API
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            retro.setBearerToken(token)
        }
        api = retro.getApi()
        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@ControlHorarioActivity, menu_administrador::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        // HORARIOS
        obtenerFechas()
        establecerAdapters()

        btnBack.setOnClickListener { moverEnSemanas(false) }
        btnNext.setOnClickListener { moverEnSemanas(true) }

//        nivelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                filtrarHorario()
//            }
//        }
//        gradoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                filtrarHorario()
//            }
//        }
        seccionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filtrarHorario()
            }
        }
    }
    private fun filtrarHorario() {
        val nivel = nivelSpinner.selectedItem.toString()[0].toString()
        val grado = gradoSpinner.selectedItem.toString()[0].toString().toInt()
        val seccion = seccionSpinner.selectedItem.toString()

        val container: ConstraintLayout = findViewById(R.id.container_recycler)
        container.visibility = View.GONE

        var cursos : List<Curso>
        api.obtenerEstudiantes(null, nivel, grado, seccion).enqueue(object : Callback<Collection<Estudiante>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Collection<Estudiante>>, response: Response<Collection<Estudiante>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val estudiantes = response.body()!!
                    if(!estudiantes.isEmpty()){
                        val estudiante = (estudiantes as MutableList)[0]
                        cursos = estudiante.itemsCurso.toList()

                        for (i in 0..<rangoFechas.size){
                            val fecha = rangoFechas[i].toString()
                            val diaNum = dias[i] + "\n" + fecha.substring(8)
                            textViewsDias[i].text = diaNum
                            val idsCursoDia = obtenerCursosXDia(i, cursos)
                            api.obtenerHorarios(fecha, idsCursoDia).enqueue(object : Callback<Collection<Horario>> {
                                override fun onResponse(call: Call<Collection<Horario>>, response: Response<Collection<Horario>>) {
                                    if (response.isSuccessful) {
                                        val horarios = response.body()!!.toList()
                                        adapters[i].setHorarios(horarios)
                                        adapters[i].notifyDataSetChanged()
                                        msg = horarios.size.toString()
                                        msg = "Total de horarios: ${msg}"
                                    }
                                }
                                override fun onFailure(call: Call<Collection<Horario>>, t: Throwable) {
                                    msg = "Error en la API: ${t.message}"
                                    Toast.makeText(this@ControlHorarioActivity, msg, Toast.LENGTH_SHORT).show()
                                    Log.e("LISTAR HORARIOS", t.message.toString())
                                }
                            } )
                        }
                        container.visibility=View.VISIBLE
                    }
                }else{
                    msg = "FAIL$msg"
                    Toast.makeText(this@ControlHorarioActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Collection<Estudiante>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(this@ControlHorarioActivity, msg, Toast.LENGTH_SHORT).show()
                Log.e("FILTRAR CURSOS", t.message.toString())
            }
        } )
    }

    private fun establecerAdapters(){
        recyclerViews = ArrayList() ; textViewsDias = ArrayList() ; adapters = ArrayList()
        val idsR = listOf(R.id.recycler_horarios_lun, R.id.recycler_horarios_mar, R.id.recycler_horarios_mie, R.id.recycler_horarios_jue, R.id.recycler_horarios_vie)
        val idsT = listOf(R.id.dia_lunes, R.id.dia_martes, R.id.dia_miercoles, R.id.dia_jueves, R.id.dia_viernes)
        for (i in idsR.indices){
            val recycler : RecyclerView = findViewById(idsR[i])
            val diaText : TextView = findViewById(idsT[i])
            val adapter = HorarioAdapter(this@ControlHorarioActivity, ArrayList())
            recycler.layoutManager = LinearLayoutManager(this)
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
        for (i in 0..4) {
            rangoFechas.add(fechaActual)
            fechaActual = fechaLunes.plusDays(i+1.toLong())
        }
    }
    private fun obtenerCursosXDia(dia: Int, cursos: List<Curso>): List<Int>{
        return cursos.filter { it.dia == dias[dia] }.map { it.cursoId }
    }
    private fun moverEnSemanas(siguiente: Boolean){
        fechaMostrar = if (siguiente) fechaMostrar.plusWeeks(1.toLong()) else fechaMostrar.minusWeeks(1.toLong())
        obtenerFechas()
        filtrarHorario()
    }
}