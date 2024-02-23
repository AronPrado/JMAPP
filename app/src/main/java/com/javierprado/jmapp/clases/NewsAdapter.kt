package com.javierprado.jmapp.clases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.clases.Noticia

class NewsAdapter(private val noticias: List<Noticia>) :
    RecyclerView.Adapter<NewsAdapter.NoticiaViewHolder>() {

    inner class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Aquí puedes encontrar las vistas en el layout de cada elemento de la lista
        val tituloTextView: TextView = itemView.findViewById(R.id.textTitulo)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textDescripcion)
        val autorFecha: TextView = itemView.findViewById(R.id.textAutorFecha)
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagenImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_noticia, parent, false)
        return NoticiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticiaActual = noticias[position]

        // Asignar los valores de la noticia a las vistas
        holder.tituloTextView.text = noticiaActual.titulo
        holder.descripcionTextView.text = noticiaActual.descripcion
        holder.autorFecha.text = noticiaActual.autor
        holder.imagenImageView.setImageResource(noticiaActual.imagen)
    }

    override fun getItemCount(): Int {
        return noticias.size
    }
}