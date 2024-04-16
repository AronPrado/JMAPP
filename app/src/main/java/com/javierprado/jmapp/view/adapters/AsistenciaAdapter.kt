package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.util.Log
import com.javierprado.jmapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.util.CursoUtil
import com.javierprado.jmapp.databinding.ItemAsistenciaAlumnoBinding
import com.javierprado.jmapp.view.clicks.AsistenciaClick

class AsistenciaAdapter(): RecyclerView.Adapter<AsistenciaAdapter.VHAsistencia>() {
    private lateinit var asistenciaClick : AsistenciaClick
    private lateinit var asistencias : MutableList<Asistencia>
    constructor(asistencias : MutableList<Asistencia>, asistenciaClick : AsistenciaClick): this() {
        this.asistencias = asistencias
        this.asistenciaClick = asistenciaClick
    }
    fun setAsistencias(asistencias: MutableList<Asistencia>){
        this.asistencias = asistencias
        notifyDataSetChanged()
    }
    class VHAsistencia(binding: ItemAsistenciaAlumnoBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemAsistenciaAlumnoBinding
        init { this.binding = binding }
        @SuppressLint("SetTextI18n")
        fun bind(asistencia: Asistencia, positionA: Int, asistencias: MutableList<Asistencia>) {
            val estudiante = asistencia.estudianteId
            Log.e("RPRPRP", estudiante)
            binding.txtAlumno.text =  estudiante.split("-")[0]

            val e = asistencia.estado ; val estado = if(e=="F") "Falta" else if(e=="T") "Tardanza" else "Asistencia"
            val indice = (binding.sAsistencia.adapter as ArrayAdapter<String>).getPosition(estado)
            binding.imgEstudianteGenero.setImageResource(R.drawable.estudiantem)
            if(estudiante.split("-")[2] == "F"){
                binding.imgEstudianteGenero.setImageResource(R.drawable.estudiantef)
            }

            binding.sAsistencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val nuevoEstado = binding.sAsistencia.selectedItem.toString()[0].toString()
                    asistencias[positionA].estado = nuevoEstado

                }
            }
            binding.sAsistencia.setSelection(indice)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHAsistencia {
        val binding = ItemAsistenciaAlumnoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHAsistencia(binding)
    }
    override fun getItemCount(): Int = asistencias.size
    override fun onBindViewHolder(holder: VHAsistencia, position: Int) {
        val asistencia = asistencias[position]

        holder.bind(asistencia, position, asistencias)
        holder.itemView.setOnClickListener { asistenciaClick.onAsistenciaClicker(asistencia) }
    }
}