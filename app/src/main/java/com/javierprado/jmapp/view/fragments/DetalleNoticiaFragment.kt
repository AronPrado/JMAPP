package com.javierprado.jmapp.view.fragments

import com.javierprado.jmapp.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.javierprado.jmapp.data.entities.Noticia

class DetalleNoticiaFragment: DialogFragment() {
    var noticia: Noticia? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.item_detalles_noticia, null)

        if(noticia != null){
            val fechaTV = view.findViewById<TextView>(R.id.fecha_noticia_detalle)
            fechaTV.text =noticia?.fechaPublicacion ?:"No hay fecha."
//        fechaTV.text = "Título del Elemento"

            val tituloTV = view.findViewById<TextView>(R.id.titulo_noticia_detalle)
            tituloTV.text =noticia?.titulo ?:"No hay descripcion."
//        contentTextView.text = "Contenido adicional..."

            val imagenTV = view.findViewById<ImageView>(R.id.img_noticia_detalle)
            val resources = context?.resources
            val mipmapIds = R.mipmap::class.java.fields.map { it.getInt(null) }

            imagenTV.setImageResource(mipmapIds.find { id ->
                val name = resources?.getResourceEntryName(id)
                name == noticia?.ubiImagen
            } ?: R.drawable.task)

            val descripcionTV = view.findViewById<TextView>(R.id.descripcion_noticia_detalle)
            descripcionTV.text =noticia?.contenido ?:"No hay descripcion."

            builder.setView(view)
                .setPositiveButton("Cerrar") { dialog, _ ->
                    dialog.dismiss() // Cierra el cuadro de diálogo al presionar el botón "Cerrar"
                }

        }
        return builder.create()
    }
}