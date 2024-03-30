package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.databinding.ItemListaHijosBinding
import com.javierprado.jmapp.view.clicks.AulaClick
import de.hdodenhof.circleimageview.CircleImageView

class HijoApoderadoAdapter(): RecyclerView.Adapter<HijoApoderadoAdapter.VHEstudiante>() {
    private var listener: onEstudianteApoClickListener? = null
    private var listOfEstudiantes = listOf<Estudiante>()

    constructor(estudiantes : List<Estudiante>, hijoClick : onEstudianteApoClickListener): this() {
        this.listOfEstudiantes = estudiantes
        this.listener = hijoClick
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHEstudiante {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_hijos, parent, false)
//        val binding = ItemListaHijosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHEstudiante(view)
    }
    override fun getItemCount(): Int = listOfEstudiantes.size
    override fun onBindViewHolder(holder: VHEstudiante, position: Int) {
        val estudiante = listOfEstudiantes[position]
        holder.hijoNombre.text = "${estudiante.nombres} ${estudiante.apellidos}"
        holder.itemView.setOnClickListener {
            listener?.onHijoSelected(estudiante) }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setEstudiantes(estudiantes: List<Estudiante?>){
        this.listOfEstudiantes = estudiantes as List<Estudiante>
        notifyDataSetChanged()
    }
    class VHEstudiante(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hijoNombre: TextView = itemView.findViewById(R.id.estudiante)
//        private val binding: ItemListaHijosBinding
//        init { this.binding = binding }
//        fun bind(estudiante: Estudiante) {
//            val nombres = estudiante.nombres + " " + estudiante.apellidos
//            val grado = estudiante.grado
//            val seccion = estudiante.seccion
//            binding.estudiante.text =  nombres
//        }
    }

}
interface onEstudianteApoClickListener{
    fun onHijoSelected(hijo: Estudiante)
}