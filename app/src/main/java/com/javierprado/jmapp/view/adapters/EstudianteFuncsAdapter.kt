package com.javierprado.jmapp.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.databinding.ItemEstudianteConFuncionesBinding
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.clicks.EstudianteFClick
import com.javierprado.jmapp.view.fragments.IngresarCalificacionesFragment
import java.io.Serializable

class EstudianteFuncsAdapter(): RecyclerView.Adapter<EstudianteFuncsAdapter.VHEstudiante>() {
    private lateinit var token : String
    private lateinit var estudiantes : List<Estudiante>
    private lateinit var docente : Docente
    private lateinit var activity : AppCompatActivity

    constructor(estudiantes : List<Estudiante>, token: String, docente: Docente, activity: AppCompatActivity): this() {
        this.token = token
        this.estudiantes = estudiantes
        this.docente = docente
        this.activity = activity
    }
    fun setDocente(docente: Docente){ this.docente = docente }
    class VHEstudiante(binding: ItemEstudianteConFuncionesBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemEstudianteConFuncionesBinding
        init { this.binding = binding }
        fun bind(estudiante: Estudiante, token: String, curso: Curso, activity: AppCompatActivity) {
            val extraF = ExtraFunctions()
//            val grado = estudiante.obtenerGrado(estudianteId)
//            val seccion = estudiante.obtenerSeccion(estudianteId)
            binding.nombreEstudiante.text = estudiante.nombres + " " + estudiante.apellidos

            val lista: List<Estudiante> = ArrayList()
            binding.btnOtraFuncion.setOnClickListener {
                Log.e("PRESIONADO", "VAS A OTRA FUNCION POR ALUMNO")
            }
            binding.btnCalificacionFuncion.setOnClickListener {
                Log.e("PRESIONADO", "VAS A PONER NOTAS")
                val fragment = IngresarCalificacionesFragment.newInstance(token,estudiante.estudianteId, curso.cursoId)
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
        holder.bind(estudiante, token, docente.curso!!, activity)
    }
}
