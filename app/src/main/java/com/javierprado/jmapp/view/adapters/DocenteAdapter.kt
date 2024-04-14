package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.util.CursoUtil

class DocenteAdapter(private val context: Context, private var docentes: List<Docente>): BaseAdapter() {

    override fun getCount(): Int  = docentes.size
    override fun getItem(position: Int): Docente = docentes[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_docente, parent, false)
        val docente = docentes[position]

        val nombres: TextView = view.findViewById(R.id.docente_itemdocente)
        val curso: TextView = view.findViewById(R.id.curso_itemdocente)
        val img: ImageView = view.findViewById(R.id.img_itemdocente)
        nombres.text = docente.nombres + " " + docente.apellidos
        val nombreCurso = docente.cursoId
        curso.text = nombreCurso
        Log.e("CURSOADAPTER",nombreCurso)
        // ESTABLECER COLORES
        view.setBackgroundColor(CursoUtil.getBackgroundColor(nombreCurso))
        nombres.setTextColor(CursoUtil.getTextColor(nombreCurso))
        curso.setTextColor(CursoUtil.getTextColor(nombreCurso))
        // ESTABLECER IMAGEN
        img.setImageResource(CursoUtil.getImg(nombreCurso))
        return view
    }

    fun setList(docentes: List<Docente>) {
        this.docentes = docentes
        notifyDataSetChanged()
    }
}