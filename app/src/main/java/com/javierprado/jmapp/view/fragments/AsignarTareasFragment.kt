package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Tarea
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeParseException

class AsignarTareasFragment : Fragment() {
    private lateinit var btnAsignar: Button
    private lateinit var descripcionTarea: EditText
    private lateinit var fechaEntrega: EditText

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var estudiantes : List<String>
    val TOKEN = "token" ; val ESTUDIANTES = "estudiantes"
    private lateinit var aulaId : String ; private lateinit var docenteId : String
    private val TAG: String  = toString()
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
            retro.setBearerToken(it.getString(TOKEN, ""))
            estudiantes = it.getStringArrayList(ESTUDIANTES)?.toList() ?: ArrayList()
            aulaId = it.getString("AULAID", "")
            docenteId = it.getString("DOCENTEID", "")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_asignar_tareas, container, false)
        btnAsignar = view.findViewById(R.id.btn_asignar_tarea)
        descripcionTarea = view.findViewById(R.id.txt_descripcion_tarea)
        fechaEntrega = view.findViewById(R.id.txt_fechaentrega_tarea)
        progressC = view.findViewById(R.id.pb_asignar_tareas)

        fun validacion(){
            val descripcion = descripcionTarea.text.toString()
            val fecha = fechaEntrega.text.toString()
            var valF: Boolean ; val valD: Boolean
            var errorF: String
            if(fecha.length == 10){
                try{
                    val fechaHoy = LocalDate.now().plusDays(1)
                    if(LocalDate.parse(fecha).isBefore(fechaHoy)){
                        errorF = "La fecha no debe ser antes que la fecha actual"
                    }else{ errorF=""}
                }catch(exc: DateTimeParseException){
                    errorF = "El formato de la fecha debe ser 'aaaa-dd-MM'"
                }
            }else if(fecha.length>10){
                errorF = "El formato de la fecha debe ser 'aaaa-dd-MM'"
            }else{
                errorF = "Completar la fecha de entrega"
            }
            valD = descripcion.isNotEmpty() && descripcion.length < 100
            descripcionTarea.error = if(!valD) "Formato de descripción no válido" else null
            valF = errorF.isEmpty()
            fechaEntrega.error = if(!valF) errorF else null
            btnAsignar.isEnabled = valF && valD
        }

        descripcionTarea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                validacion()
            }
        })
        fechaEntrega.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                validacion()
            }
        })
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()


        btnAsignar.setOnClickListener{
            val descripcion = descripcionTarea.text.toString()
            val fecha = fechaEntrega.text.toString()

            val tarea = Tarea(descripcion, fecha, aulaId, docenteId)
            progressC.visibility=View.VISIBLE
            api.agregarTarea(tarea).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) {
                        progressC.visibility=View.GONE
                        activity.supportFragmentManager.popBackStackImmediate()
                    }
                    else{ Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("AGREGAR TAREA", t.message.toString())
                }
            } )
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiantes: Serializable) =
            AsignarTareasFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(ESTUDIANTES, estudiantes)
                }
            }
    }
}