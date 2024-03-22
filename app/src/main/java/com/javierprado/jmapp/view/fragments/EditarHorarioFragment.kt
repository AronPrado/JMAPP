package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
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
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TOKEN = "token"
private const val HORARIO_ID = "ID"
class EditarHorarioFragment : DialogFragment() {

    private lateinit var txtHInicio: EditText
    private lateinit var txtHFin: EditText
    private lateinit var txtCurso: TextView
    private lateinit var txtFecha: TextView
    private lateinit var txtDocente: TextView
    private lateinit var btnEdit: Button

    private lateinit var horarioUpd: Horario

    private var horario_id = 0

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
            retro.setBearerToken(it.getString(TOKEN, ""))
            horario_id =  it.getInt(HORARIO_ID, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_editar_horario, container, false)

        txtHInicio = view.findViewById(R.id.txt_hinicio_horario)
        txtHFin = view.findViewById(R.id.txt_hfin_horario)
        txtFecha = view.findViewById(R.id.fg_date_horario)
        txtCurso = view.findViewById(R.id.fg_curso_horario)
        btnEdit = view.findViewById(R.id.fg_btn_editarh)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()
        api.buscarHorario(horario_id).enqueue(object : Callback<Horario> {
            override fun onResponse(call: Call<Horario>, response: Response<Horario>) {
                if (response.isSuccessful) {
                    val horario = response.body()!!
                    horarioUpd = horario
                    txtHInicio.setText(horario.horaInicio)
                    txtHFin.setText(horario.horaFin)
                    txtCurso.text = horario.curso.nombre.toString()
                    txtFecha.text = horario.fechaClase.toString()
                    txtDocente.text="Prueba Docente"

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
            // Validate the information

            if (txtInicio.isEmpty() || txtFin.isEmpty()) {
                Toast.makeText(requireContext(), "Los campos no deben estar vacios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            api.actualizarHorario(horarioUpd).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers().get("message").toString()
                    if (response.isSuccessful) {
                        val fragment = HorarioFragment.newInstance(retro.getBearerToken(), true, ArrayList())
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fcv_horario_main, fragment).commit()
                    }else{
                        Log.e("NR ACTUALIZAR HORARIO", msg)
                    }
                    Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()

                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    msg = "Error en la API: ${t.message}"
                    Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("ACTuALIZAR HORARIO", t.message.toString())
                }
            } )
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(horario: Horario, token: String) =
            EditarHorarioFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(HORARIO_ID, horario.horarioId)
                }
            }
    }
}