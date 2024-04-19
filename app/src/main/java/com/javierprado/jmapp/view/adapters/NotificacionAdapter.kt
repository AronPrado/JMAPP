package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.data.entities.Notificacion
import com.javierprado.jmapp.databinding.ItemNotificacionUsuarioBinding
import com.javierprado.jmapp.view.clicks.NotificacionClick

class NotificacionAdapter(): RecyclerView.Adapter<NotificacionAdapter.VHNotificacion>() {
    private lateinit var notiClick : NotificacionClick
    private var notificaciones : MutableList<Notificacion> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setNotificaciones(notis: MutableList<Notificacion>){ this.notificaciones = notis ; notifyDataSetChanged() }
    @SuppressLint("NotifyDataSetChanged")
    fun setClicker(clicker: NotificacionClick){ this.notiClick = clicker ; notifyDataSetChanged() }

    class VHNotificacion(binding: ItemNotificacionUsuarioBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: ItemNotificacionUsuarioBinding
        init { this.binding = binding }
        fun bind(noti: Notificacion) {
            val titulo = noti.titulo
            val fecha = noti.fecha ; val hora = noti.hora
            binding.tituloNoti.text = titulo
            binding.detalleNoti.text = fecha + " • " + hora
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHNotificacion {
        val binding = ItemNotificacionUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VHNotificacion(binding)
    }

    override fun getItemCount(): Int = notificaciones.size

    fun getUltimoID(): String = notificaciones[itemCount-1].id

    override fun onBindViewHolder(holder: VHNotificacion, position: Int) {
        val noti = notificaciones[position]
        holder.bind(noti)
        holder.itemView.setOnClickListener { notiClick.onNotiClicker(noti) }
    }
}
