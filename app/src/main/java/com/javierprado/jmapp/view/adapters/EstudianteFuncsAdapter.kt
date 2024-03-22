package com.javierprado.jmapp.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.databinding.ItemEstudianteConFuncionesBinding
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.clicks.EstudianteFClick
import java.io.Serializable

class EstudianteFuncsAdapter(): RecyclerView.Adapter<EstudianteFuncsAdapter.VHEstudiante>() {
    private lateinit var token : String
    private lateinit var estudiantes : List<Estudiante>
    private lateinit var activity : AppCompatActivity

    constructor(estudiantes : List<Estudiante>, token: String, activity: AppCompatActivity): this() {
        this.estudiantes = estudiantes
        this.token = token
        this.activity = activity
    }
    fun setEstudiantes(estudiantes: List<Estudiante>){ this.estudiantes = estudiantes }
    class VHEstudiante(binding: ItemEstudianteConFuncionesBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemEstudianteConFuncionesBinding
        init { this.binding = binding }
        fun bind(estudiante: Estudiante, token: String, activity: AppCompatActivity) {
            val extraF = ExtraFunctions()
//            val grado = estudiante.obtenerGrado(estudianteId)
//            val seccion = estudiante.obtenerSeccion(estudianteId)
            binding.nombreEstudiante.text = estudiante.nombres + " " + estudiante.apellidos

            val lista: List<Estudiante> = ArrayList()
            binding.btnOtraFuncion.setOnClickListener {
                Log.e("PRESIONADO", "VAS A OTRA FUNCION POR ALUMNO")
            }
            binding.btnCalificacionFuncion.setOnClickListener {
                Log.e("PRESIONADO", "VAS A PONER NOTA POR ALUMNO")
                val fragment = extraF.obtenerFragment(NavigationWindows.NOTAS.name, token, lista as Serializable)
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_docente_main, fragment).addToBackStack("NOTAS").commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHEstudiante {
        val binding = ItemEstudianteConFuncionesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHEstudiante(binding)
    }

    override fun getItemCount(): Int = estudiantes.size

    override fun onBindViewHolder(holder: VHEstudiante, position: Int) {
        val estudiante = estudiantes[position]
        holder.bind(estudiante, token, activity)
//        holder.itemView.setOnClickListener { estudianteClick.onEstudianteFClicker(estudiante) }
    }
}
