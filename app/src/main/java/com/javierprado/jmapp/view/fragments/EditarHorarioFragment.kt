package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

private const val HORARIO_ID = "ID"
class EditarHorarioFragment : DialogFragment() {
    private lateinit var txtHInicio: EditText
    private lateinit var txtHFin: EditText
    private lateinit var txtCurso: TextView
    private lateinit var txtDocente: TextView
    private lateinit var txtFecha: TextView
    private lateinit var btnEdit: Button

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var horarioUpd: Horario

    private lateinit var horarioId: String

    private lateinit var msg : String
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlHorarioActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(MenuAdministradorActivity().TOKEN, ""))
            horarioId =  it.getString(HORARIO_ID, "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_editar_horario, container, false)

        txtHInicio = view.findViewById(R.id.txt_hinicio_horario)
        txtHFin = view.findViewById(R.id.txt_hfin_horario)
        txtFecha = view.findViewById(R.id.fg_date_horario)
        txtCurso = view.findViewById(R.id.txt_curso_horario)
        txtDocente = view.findViewById(R.id.txt_docente_horario)
        btnEdit = view.findViewById(R.id.fg_btn_funcion)
        progressC = view.findViewById(R.id.pb_editar_horario)
        fun validarTiempo(time: String): Boolean{
            val regex = Regex("^([0][8-9]|[1][0-3]):[0-5][0-9]\$")
            return regex.matches(time)
        }
        fun validarOrden(t1: String, t2: String, novMins: Boolean = false): Boolean{
            if(t1.contains(":") && t2.contains(":")){
                var validacion: Boolean
                val h1 = LocalTime.parse(t1) ; val h2 = LocalTime.parse(t2)
                validacion = h1.isBefore(h2)
                if(novMins){
                    validacion = h2.minusMinutes(90) == h1
                }
                return validacion
            }else{ return t1.isEmpty() || t2.isEmpty() }
        }

        txtHInicio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                var validacion = validarTiempo(s.toString())
                btnEdit.isEnabled = validacion && validarTiempo(txtHFin.text.toString())
                if (!validacion) { txtHInicio.error = "Formato de hora no válido" }
                else{
                    val hi = txtHInicio.text.toString(); val hf = txtHFin.text.toString()
                    validacion = validarOrden(hi, hf)
                    if(!validacion){
                        txtHInicio.error = "La hora debe ser antes de la hora final."
                    }else{
                        validacion = validarOrden(hi, hf, true)
                        if(!validacion){
                            txtHInicio.error = "El tiempo del curso debe ser de 90 minutos"
                        }
                    }
                    btnEdit.isEnabled = validacion
                }
            }
        })
        txtHFin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                var validacion = validarTiempo(s.toString())
                btnEdit.isEnabled= validacion && validarTiempo(txtHInicio.text.toString())
                if (!validacion) { txtHFin.error = "Formato de hora no válido" }
                else{
                    val hi = txtHInicio.text.toString(); val hf = txtHFin.text.toString()
                    validacion = validarOrden(hi, hf)
                    if(!validacion){
                        txtHFin.error = "La hora final debe ser después de la hora de inicio."
                    }else{
                        validacion = validarOrden(hi, hf, true)
                        if(!validacion){
                            txtHFin.error = "El tiempo del curso debe ser de 90 minutos"
                        }
                    }
                    btnEdit.isEnabled = validacion
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()
        progressC.visibility=View.VISIBLE
        api.buscarHorario(horarioId).enqueue(object : Callback<Horario> {
            override fun onResponse(call: Call<Horario>, response: Response<Horario>) {
                if (response.isSuccessful) {
                    val horario = response.body()!!
                    horarioUpd = horario
                    txtHInicio.setText(horario.horaInicio)
                    txtHFin.setText(horario.horaFin)
                    txtCurso.text = horario.cursoId
                    txtDocente.text = horario.docenteId
                    txtFecha.text = horario.fechaClase
                    progressC.visibility=View.GONE

                }else{
                    Log.e("NR BUSCAR HORARIO", msg)
                    Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<Horario>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                Log.e("BUSCAR HORARIO", t.message.toString())
            }
        } )

        btnEdit.setOnClickListener {
            val txtInicio = txtHInicio.text.toString()
            val txtFin = txtHFin.text.toString()

            horarioUpd.horaInicio=txtInicio
            horarioUpd.horaFin=txtFin

            if (txtInicio.isEmpty() || txtFin.isEmpty()) {
                Toast.makeText(requireContext(), "Los campos no deben estar vacios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressC.visibility=View.VISIBLE
            api.editarHorario(horarioUpd, horarioId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers().get("message").toString()
                    if (response.isSuccessful) {
                        progressC.visibility=View.GONE ;
                        activity.supportFragmentManager.popBackStackImmediate()
                    } else{ Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show() }

                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ACTUALIZAR HORARIO", t.message.toString())
                }
            } )
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(horario: Horario, token: String) =
            EditarHorarioFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(HORARIO_ID, horario.id)
                }
            }
    }
}