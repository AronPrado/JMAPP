package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.databinding.ItemDiaHorarioBinding
import com.javierprado.jmapp.view.clicks.HorarioClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HorarioAdapter() : RecyclerView.Adapter<HorarioAdapter.VHHorario>() {
    private lateinit var horarioClick : HorarioClick
    private lateinit var horarios : List<Horario>
    private lateinit var docentes : List<Docente>
    private lateinit var api: ColegioAPI

    constructor(horarios : List<Horario>, api: ColegioAPI, horarioClick : HorarioClick) :this() {
        this.horarios = horarios
        this.api = api
        this.horarioClick = horarioClick
    }
    fun setHorarios(horarios : List<Horario> ){ this.horarios = horarios }
    override fun getItemCount(): Int = horarios.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHorario {
        val binding = ItemDiaHorarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHHorario(binding)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VHHorario, position: Int) {
        val horario = horarios[position]

        holder.bind(horario, position, api)
        holder.itemView.setOnClickListener { horarioClick.onHorarioClicker(horario) }
    }

    class VHHorario(binding: ItemDiaHorarioBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemDiaHorarioBinding
        init { this.binding = binding }


        fun bind(horario: Horario, position: Int, api: ColegioAPI) {
            binding.cursoHorario.text = horario.curso.nombre
            val horas = horario.horaInicio?.subSequence(0, 5).toString() + "\n-\n" + horario.horaFin?.subSequence(0, 5).toString()
            binding.horasHorario.text = horas
//            obtenerDocente(horario.curso.cursoId, position, api, binding.docenteHorario)
        }
    }
}
private fun obtenerDocente(cursoId: Int, position: Int, api: ColegioAPI, txt: TextView){
    var msg : String
    api.obtenerDocentes(cursoId, null).enqueue(object : Callback<Collection<Docente>> {
        override fun onResponse(call: Call<Collection<Docente>>, response: Response<Collection<Docente>>) {
            msg = response.headers()["message"] ?: ""
            if (response.isSuccessful) {
                val docentes = response.body()!! as MutableList
                msg += " ${docentes.size}"
                txt.text = docentes[position].nombres + " " + docentes[position].apellidos
            }else{
                txt.text = "Sin docente."
            }
            Log.e("RESPUESTA", msg)
        }
        override fun onFailure(call: Call<Collection<Docente>>, t: Throwable) {
            msg = "Error en la API: ${t.message}"
            Log.e("LISTAR DOCENTES", t.message.toString())
        }
    } )
}