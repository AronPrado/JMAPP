package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.databinding.ItemAsistenciaAlumnoBinding
import com.javierprado.jmapp.view.clicks.AsistenciaClick

class AsistenciaAdapter(): RecyclerView.Adapter<AsistenciaAdapter.VHAsistencia>() {
    private lateinit var asistenciaClick : AsistenciaClick
    private lateinit var asistencias : List<Asistencia>

    constructor(asistencias : List<Asistencia>, asistenciaClick : AsistenciaClick): this() {
        this.asistencias = asistencias
        this.asistenciaClick = asistenciaClick
    }
    fun setAsistencias(asistencias: List<Asistencia>){
        this.asistencias = asistencias
    }
    class VHAsistencia(binding: ItemAsistenciaAlumnoBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemAsistenciaAlumnoBinding
        init { this.binding = binding }
        @SuppressLint("SetTextI18n")
        fun bind(asistencia: Asistencia) {
            val estudiante = asistencia.itemsEstudiante.toList()[0]
            binding.txtAlumno.text =  estudiante.nombres + " " + estudiante.apellidos
//            binding.sAsistencia.

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHAsistencia {
        val binding = ItemAsistenciaAlumnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHAsistencia(binding)
    }

    override fun getItemCount(): Int = asistencias.size

    override fun onBindViewHolder(holder: VHAsistencia, position: Int) {
        val asistencia = asistencias[position]

        holder.bind(asistencia)
        holder.itemView.setOnClickListener { asistenciaClick.onAsistenciaClicker(asistencia) }
    }
}