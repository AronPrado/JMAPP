package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.databinding.ItemNoticiaBinding
import com.javierprado.jmapp.view.activities.control.ControlNoticiaActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.clicks.NoticiaClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticiaAdapter(): RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder>() {
    private lateinit var clicker: NoticiaClick
    private lateinit var noticias: List<Noticia>
    private lateinit var context: Context
    private lateinit var api: ColegioAPI
    private lateinit var token: String
    private var adminId: String = ""
    private var forDA = false

    constructor(context: Context, noticias: List<Noticia>, api: ColegioAPI, token: String, forDA: Boolean): this(){
        this.context = context
        this.noticias = noticias
        this.context = context
        this.api = api
        this.token = token
        this.forDA = forDA
    }
    fun setNoticias(noticias : List<Noticia> ){
        this.noticias = noticias
    }
    fun setAdminId(adminId : String ){ this.adminId = adminId }
    fun getAdminId(): String{ return adminId }
    fun setClicker(clicker: NoticiaClick){
        this.clicker = clicker
    }
    override fun getItemCount(): Int {
        return noticias.size
    }
    inner class NoticiaViewHolder(binding: ItemNoticiaBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemNoticiaBinding
        init { this.binding = binding }
        fun bind(noticia: Noticia) {
            binding.txtTituloNoticia.text = noticia.titulo
            binding.txtContenidoNoticia.text = noticia.contenido
            binding.txtFechaNoticia.text = noticia.fechaPublicacion

            val resources = context.resources
            val mipmapIds = R.mipmap::class.java.fields.map { it.getInt(null) }

            binding.imgNoticia.setImageResource(mipmapIds.find { id ->
                val name = resources.getResourceEntryName(id)
                name == noticia.ubiImagen
            } ?: R.drawable.task)
            //BOTONES
            binding.editarNoticia.visibility = if(!forDA) View.VISIBLE else View.GONE
            binding.eliminarNoticia.visibility = if(!forDA) View.VISIBLE else View.GONE

            if(!forDA){
                binding.editarNoticia.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                        .setTitle("Noticia")
                        .setMessage("¿Estás seguro de editar esta noticia?")
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Sí") { d, _ ->
                            val intent = Intent(context, ControlNoticiaActivity::class.java)
                            intent.putExtra(MenuAdministradorActivity().TOKEN, token)
                            intent.putExtra(MenuAdministradorActivity().USUARIOID, adminId)
                            intent.putExtra(ControlNoticiaActivity().NOTID, noticia.id)
                            context.startActivity(intent)
                        }
                    val alert = builder.create()
                    alert.show()
                }
                binding.eliminarNoticia.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                        .setTitle("Noticia")
                        .setMessage("¿Estás seguro de eliminar esta noticia?")
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Sí") { dialog, _ ->
                            var msg: String
                            noticia.id.let { it1 ->
                                api.eliminarNoticia(it1).enqueue(object : Callback<Void> {
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        msg = response.headers()["message"] ?: ""
                                        if(response.isSuccessful){
                                            val principal = context as MenuAdministradorActivity
                                            principal.actualizarNoticias(token)
                                        }else{
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val binding = ItemNoticiaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticiaViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = noticias[position]
        holder.bind(noticia)
        holder.itemView.setOnClickListener { clicker.onNoticiaClicker(noticia) }
    }
}