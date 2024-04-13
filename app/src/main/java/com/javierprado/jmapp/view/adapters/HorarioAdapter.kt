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
    private lateinit var api: ColegioAPI

    constructor(horarios : List<Horario>, api: ColegioAPI, horarioClick : HorarioClick): this() {
        this.horarios = horarios
        this.api = api
        this.horarioClick = horarioClick
    }
    fun setHorarios(horarios : List<Horario> ){
        this.horarios = horarios
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = horarios.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHHorario {
        val binding = ItemDiaHorarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHHorario(binding)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VHHorario, position: Int) {
        val horario = horarios[position]

        horario.cursoId = horario.cursoId.split("-")[0] // NOMBRES
        horario.docenteId = horario.docenteId.split("-")[0] // NOMBRES
        holder.bind(horario)
        holder.itemView.setOnClickListener { horarioClick.onHorarioClicker(horario) }
    }

    class VHHorario(binding: ItemDiaHorarioBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemDiaHorarioBinding
        init { this.binding = binding }
        fun bind(horario: Horario) {
            binding.cursoHorario.text = horario.cursoId
            binding.docenteHorario.text = horario.docenteId
            val horas = horario.horaInicio + "\n-\n" + horario.horaFin
            binding.horasHorario.text = horas
        }
    }
}