package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante

class EstudianteAdapter(private val context: Context, private val estudiantes: MutableList<Estudiante>) :
    BaseAdapter() {

    override fun getCount(): Int = estudiantes.size

    override fun getItem(position: Int): Estudiante = estudiantes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.items_estudiante__registrar_apoderado, parent, false)
        val estudiante = estudiantes[position]

        val textViewEstudiante: TextView = view.findViewById(R.id.txtnomsaps_estudiante__registrar_apoderado)
        val btnEliminar: Button = view.findViewById(R.id.btnnEliminar__registrar_apoderado)

        textViewEstudiante.text = "${estudiante.nombres} ${estudiante.apellidos}"

        btnEliminar.setOnClickListener {
            estudiantes.removeAt(position)
            notifyDataSetChanged()
        }
        return view
    }
}