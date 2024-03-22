package com.javierprado.jmapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.databinding.ItemDetallesAulaBinding
import com.javierprado.jmapp.view.clicks.AulaClick
import com.javierprado.jmapp.view.clicks.HorarioClick

class AulaAdapter(): RecyclerView.Adapter<AulaAdapter.VHAula>() {
    private lateinit var aulaClick : AulaClick
    private lateinit var aulas : List<Aula>

    constructor(aulas : List<Aula>, aulaClick : AulaClick): this() {
        this.aulas = aulas
        this.aulaClick = aulaClick
    }
    fun setAulas(aulas: List<Aula>){ this.aulas = aulas }
    class VHAula(binding: ItemDetallesAulaBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemDetallesAulaBinding
        init { this.binding = binding }
        fun bind(aula: Aula) {
            val estudianteId = aula.estudiantes.toList()[0].estudianteId
            val grado = aula.obtenerGrado(estudianteId)
            val seccion = aula.obtenerSeccion(estudianteId)
            binding.gradoSeccionAula.text =  "${grado}° '${seccion}'"
            binding.totalAula.text = aula.totalEstudiantes().toString() + " estudiantes"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHAula {
        val binding = ItemDetallesAulaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHAula(binding)
    }

    override fun getItemCount(): Int = aulas.size

    override fun onBindViewHolder(holder: VHAula, position: Int) {
        val aula = aulas[position]
        holder.bind(aula)
        holder.itemView.setOnClickListener { aulaClick.onAulaClicker(aula) }
    }
}
