package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso

class CursoAdapter(private val context: Context, private val cursos: MutableList<Curso>) :
    BaseAdapter() {

    override fun getCount(): Int = cursos.size

    override fun getItem(position: Int): Curso = cursos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.items_curso__registrar_docente, parent, false)
        val curso = getItem(position)

        val textViewCurso: TextView = view.findViewById(R.id.txtcurso__registrar_docente)
        textViewCurso.text = curso.nombre
        return view
    }
}