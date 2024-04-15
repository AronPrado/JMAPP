package com.javierprado.jmapp.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.databinding.ItemInfoEstudianteBinding
import com.javierprado.jmapp.view.clicks.EstudianteFClick
import com.javierprado.jmapp.R

class EstudianteSeleccionAdapter(): RecyclerView.Adapter<EstudianteSeleccionAdapter.VHEstudiante>() {
    private lateinit var estudianteClick : EstudianteFClick
    private lateinit var estudiantes : List<Estudiante>

    constructor(estudiantes : List<Estudiante>, estudianteClick : EstudianteFClick): this() {
        this.estudiantes = estudiantes
        this.estudianteClick = estudianteClick
    }
    fun setEstudiantes(estudiantes: List<Estudiante>){
        this.estudiantes = estudiantes
        notifyDataSetChanged()
    }
    
    class VHEstudiante(binding: ItemInfoEstudianteBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemInfoEstudianteBinding
        init { this.binding = binding }
        fun bind(estudiante: Estudiante) {
            val grado = estudiante.grado ; val seccion = estudiante.seccion
            val nivel = estudiante.nivelEducativo;
            val info = "$grado° '$seccion' - $nivel"
            binding.gradoSeccionEstudiante.text = info
            binding.nombreEstudiante.text = estudiante.nombres + " " + estudiante.apellidos
            //PONER IMG DE ACUERDO AL GENERO
            binding.imgGeneroEstudiante.setImageResource(R.drawable.estudiantef)
            if(estudiante.genero == "M"){ binding.imgGeneroEstudiante.setImageResource(R.drawable.estudiantem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHEstudiante {
        val binding = ItemInfoEstudianteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHEstudiante(binding)
    }

    override fun getItemCount(): Int = estudiantes.size

    override fun onBindViewHolder(holder: VHEstudiante, position: Int) {
        val estudiante = estudiantes[position]
        holder.bind(estudiante)
        holder.itemView.setOnClickListener { estudianteClick.onEstudianteFClicker(estudiante) }
    }
}