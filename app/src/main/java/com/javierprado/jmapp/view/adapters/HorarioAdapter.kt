package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.clases.NewsAdapter
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.view.agregar.ControlHorarioActivity

class HorarioAdapter(private val context: Context, private var horarios: List<Horario>) :
    RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {
    fun setHorarios(horarios : List<Horario> ){
        this.horarios = horarios
    }
    override fun getItemCount(): Int = horarios.size

    inner class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cursoText: TextView = itemView.findViewById(R.id.curso_horario)
        val horasText: TextView = itemView.findViewById(R.id.horas_horario)
        val docenteText: TextView = itemView.findViewById(R.id.docente_horario)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioAdapter.HorarioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_horario, parent, false)
        return HorarioViewHolder(itemView)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HorarioAdapter.HorarioViewHolder, position: Int) {
        val horario = horarios[position]

        holder.cursoText.text = horario.curso.nombre
        val horas = horario.horaInicio?.subSequence(0, 5).toString() + "\n-\n" + horario.horaFin?.subSequence(0, 5).toString()
        holder.horasText.text = horas
        holder.docenteText.text = obtenerDocente(horario.curso.cursoId)

    }

    private fun obtenerDocente(cursoId: Int): String{
        val docente = Docente()
//        return "${docente.nombres} ${docente.apellidos}"
        return "Carlos Acosta"
    }
    fun onHorarioClick(view: View) {
//        val clickedView = view as TextView
//        val horasText = clickedView.text.toString()
//
//        val horasSplit = horasText.split("\n")
//        val startTime = horasSplit[0]
//        val endTime = horasSplit[2]
//
//        val courseName =
//
//        // Create and show the EditHorarioDialogFragment
//        val dialogFragment = EditHorarioDialogFragment(courseName, startTime, endTime)
//        dialogFragment.show(supportFragmentManager, "editar_horario")
    }
}