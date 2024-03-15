package com.javierprado.jmapp.clases

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.agregar.ControlNoticiaActivity
import com.javierprado.jmapp.view.menu_administrador
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsAdapter(private val context: Context, private var noticias: List<Noticia>, private val api: ColegioAPI, private val token: String,
    private val forDA: Boolean) :
    RecyclerView.Adapter<NewsAdapter.NoticiaViewHolder>() {
    fun setNoticias(noticias : List<Noticia> ){
        this.noticias = noticias
    }
    override fun getItemCount(): Int {
        return noticias.size
    }
    inner class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Aquí puedes encontrar las vistas en el layout de cada elemento de la lista
        val tituloTextView: TextView = itemView.findViewById(R.id.txt_titulo_noticia)
        val contenidoTextView: TextView = itemView.findViewById(R.id.txt_contenido_noticia)
        val fechaTextView: TextView = itemView.findViewById(R.id.txt_fecha_noticia)
        val imgImageView: ImageView = itemView.findViewById(R.id.img_noticia)

        val btnEdit: ImageView = itemView.findViewById(R.id.editar_noticia)
        val btnDelete: ImageView = itemView.findViewById(R.id.eliminar_noticia)
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
        holder.contenidoTextView.text = noticiaActual.contenido
        holder.fechaTextView.text = noticiaActual.fechaPublicacion

        val resources = context.resources
        val mipmapIds = R.mipmap::class.java.fields.map { it.getInt(null) }

        holder.imgImageView.setImageResource(mipmapIds.find { id ->
            val name = resources.getResourceEntryName(id)
            name == noticiaActual.ubiImagen
        } ?: R.drawable.task)
        //BOTONES
        holder.btnEdit.visibility = if(!forDA) View.VISIBLE else View.GONE
        holder.btnDelete.visibility = if(!forDA) View.VISIBLE else View.GONE
        if(!forDA){
            holder.btnEdit.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                    .setTitle("Noticia")
                    .setMessage("¿Estás seguro de editar esta noticia?")
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Sí") { dialog, _ ->
                        val intent = Intent(context, ControlNoticiaActivity::class.java)
                        intent.putExtra(ControlNoticiaActivity().TOKEN, token)
                        intent.putExtra(ControlNoticiaActivity().NOTID, noticiaActual.noticiaId)
                        context.startActivity(intent)
                    }
                val alert = builder.create()
                alert.show()
            }
            holder.btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                    .setTitle("Noticia")
                    .setMessage("¿Estás seguro de eliminar esta noticia?")
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Sí") { dialog, _ ->
                        var msg = ""
                        noticiaActual.noticiaId?.let { it1 ->
                            api.eliminarNoticiaPorId(it1).enqueue(object : Callback<Void> {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    msg = response.headers()["message"] ?: ""
                                    val principal = context as menu_administrador
                                    principal.actualizarNoticias(token)
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    msg = "Error en la API"
                                    Log.e(msg, t.message.toString())
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                val alert = builder.create()
                alert.show()
            }
        }
    }
}