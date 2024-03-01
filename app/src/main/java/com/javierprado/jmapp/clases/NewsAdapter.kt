package com.javierprado.jmapp.clases

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Noticia

class NewsAdapter(private var noticias: List<Noticia>) :
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticiaActual = noticias[position]

        holder.tituloTextView.text = noticiaActual.titulo
        holder.descripcionTextView.text = noticiaActual.contenido
        holder.autorFecha.text = noticiaActual.fechaPublicacion + " - " + noticiaActual.administrador?.nombres + " " +noticiaActual.administrador?.nombres
        holder.imagenImageView.setImageResource(R.drawable.task)
    }

    override fun getItemCount(): Int {
        return noticias.size ?: 0
    }
    fun setNoticias(noticias : List<Noticia> ){
        this.noticias = noticias
    }
}