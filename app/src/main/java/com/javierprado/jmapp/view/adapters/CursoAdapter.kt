package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.view.activities.agregar.RegisterDocenteActivity

class CursoAdapter(private val context: Context, private val cursos: MutableList<Curso>) :
    BaseAdapter() {
    private var selectedItem = -1
    override fun getCount(): Int = cursos.size

    override fun getItem(position: Int): Curso = cursos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.items_curso__registrar_docente, parent, false)
        val curso = getItem(position)

        val textViewCurso: TextView = view.findViewById(R.id.txtcurso__registrar_docente)
        val textViewDiaCurso: TextView = view.findViewById(R.id.txtdiacurso__registrar_docente)
        textViewCurso.text = curso.nombre
        textViewDiaCurso.text = " - ${curso.dia}"

        if (position == selectedItem) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.color2))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        // Establecer el evento de clic en el elemento de la lista
        view.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
            (context as RegisterDocenteActivity).onCursoSelected(curso)
        }
        return view
    }
}