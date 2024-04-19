package com.javierprado.jmapp.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Notificacion
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.databinding.FragmentNotificacionesUsuarioBinding
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.adapters.NotificacionAdapter
import com.javierprado.jmapp.view.login.OptionLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Random

class NotificacionesUsuarioFragment : DialogFragment() {
    lateinit var binding: FragmentNotificacionesUsuarioBinding
    private lateinit var activity: AppCompatActivity

    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var api: ColegioAPI
    private lateinit var msg : String

    lateinit var adapterN: NotificacionAdapter ; lateinit var adapterP: NotificacionAdapter
    lateinit var usuarioId: String ; lateinit var fecha: LocalDate
    private var cargando = false ; private var cargadas = true
    var nuevas: MutableList<Notificacion> = ArrayList()
    var pasadas: MutableList<Notificacion> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cambiarContext(it.getString(OptionLogin().ROL, ""))
            usuarioId = it.getString(MenuAdministradorActivity().USUARIOID, "")

            retro.setBearerToken(it.getString(MenuAdministradorActivity().TOKEN, ""))
            api = retro.getApi()
        }
        fecha = LocalDate.now()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificacionesUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance(usuarioId: String, rol: String, token: String) =
            NotificacionesUsuarioFragment().apply {
                arguments = Bundle().apply {
                    putString(MenuAdministradorActivity().USUARIOID, usuarioId)
                    putString(MenuAdministradorActivity().TOKEN, token)
                    putString(OptionLogin().ROL, rol)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actuales.layoutManager = LinearLayoutManager(activity)
        val layoutManager = LinearLayoutManager(activity)
        binding.pasadas.layoutManager = layoutManager

        adapterN = NotificacionAdapter() ; adapterP = NotificacionAdapter()
        binding.actuales.adapter=adapterN ; binding.pasadas.adapter=adapterP

        binding.pasadas.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!cargando) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && cargadas) {
                        cargarMasNotificaciones(adapterP.getUltimoID())
                    }
                }
            }
        })
        buscarNotificacionesIniciales(usuarioId)
    }

    private fun cargarMasNotificaciones(ultimaNotificacion: String) {
//        if(cargadas){
//            msg = "No hay más noticias."
//            val fechas = listOf(fecha.minusDays(1),fecha.minusDays(3), fecha.minusDays(4))
//            val titulo = "Notificacion de mas atras"
//            val horas = generarHoras(3)
//            for(i in 0..2){
//                val n = Notificacion(); n.fecha= fechas[i].toString() ; n.hora=horas[i] ; n.titulo=titulo+i.toString()
//                pasadas.add(n)
//            }
//            adapterP.setNotificaciones(pasadas)
//            cargadas = false
        if(cargadas){
            cargadas = false;
            api.listarNotificaciones(usuarioId, ultimaNotificacion).enqueue(object : Callback<List<Notificacion>> {
                override fun onResponse(call: Call<List<Notificacion>>, response: Response<List<Notificacion>>) {
                    msg = response.headers()["message"] ?: ""
                    cargadas = response.headers()["extra"].equals("true")
                    if (response.isSuccessful) {
                        val notificaciones = response.body()!!
                        if(notificaciones.isNotEmpty()){ pasadas.addAll(notificaciones) }
                    }else{
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        Log.e("FAIL", msg)
                    }
                }
                override fun onFailure(call: Call<List<Notificacion>>, t: Throwable) {
                    Log.e("NOTIFICACIONES", t.message.toString())
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

                }
            })
        }else{
            Toast.makeText(context, "No hay más notificaciones.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarNotificacionesIniciales(usuarioId: String) {
//        val fechas = listOf(fecha,fecha, fecha,fecha, fecha) + generarFechaAlAzar(notisInit.size)
//        val titulo = "Notificacion de prueba"
//        val horas = generarHoras(notisInit.size)
//        notisInit.mapIndexed { i, n->n.fecha=fechas[i].toString(); n.titulo=titulo+(i+1).toString();n.hora=horas[i] }
//        nuevas = notisInit.filter { n-> fecha == LocalDate.parse(n.fecha) } as MutableList
//        pasadas = notisInit.filter{ n->LocalDate.parse(n.fecha).isBefore(fecha) } as MutableList
//        Log.e("NUEVAS",nuevas.size.toString())
//        Log.e("PASADAS",pasadas.size.toString())
        adapterN.setNotificaciones(nuevas); adapterP.setNotificaciones(pasadas)
        api.listarNotificaciones(usuarioId, "null").enqueue(object : Callback<List<Notificacion>> {
            override fun onResponse(call: Call<List<Notificacion>>, response: Response<List<Notificacion>>) {
                msg = response.headers()["message"] ?: ""
                cargadas = response.headers()["extra"].equals("true")
                if (response.isSuccessful) {
                    val notificaciones = response.body()!!
                    nuevas = notificaciones.filter { n->fecha.equals(LocalDate.parse(n.fecha)) } as MutableList
                    pasadas = notificaciones.filter{ n->LocalDate.parse(n.fecha).isBefore(fecha) } as MutableList
                    adapterN.setNotificaciones(nuevas); adapterP.setNotificaciones(pasadas)
                }else{
                    dismiss()
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e("FAIL", msg)
                }
            }
            override fun onFailure(call: Call<List<Notificacion>>, t: Throwable) {
                Log.e("NOTIFICACIONES", t.message.toString())
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun cambiarContext(rol: String){
        activity = when (rol) {
            RoleType.DOC.name -> { context as MenuDocenteActivity }
            RoleType.APOD.name -> { context as MenuApoderadoActivity }
            else -> { context as MenuAdministradorActivity }
        }
    }
    fun generarFechaAlAzar(i: Int): List<LocalDate> {
        val random = Random()

        var fechas: MutableList<LocalDate> = ArrayList()
        for (n in 0..i+1){
            // Genera un año aleatorio entre 1970 y el año actual
            val year = 2023
            // Genera un mes aleatorio (1-12)
            val month = random.nextInt(3) + 1

            // Genera un día aleatorio (1-28, 30 o 31, dependiendo del mes)
            val maxDayOfMonth = LocalDate.of(year, month, 1).lengthOfMonth()
            val day = random.nextInt(maxDayOfMonth) + 1
            fechas.add(LocalDate.of(year, month, day))
        }
        return fechas
    }
    fun generarHoras(i: Int): List<String> {
        val random = Random()
        var horas: MutableList<String> = ArrayList()
        for (n in 0..i+1){
            // Genera un año aleatorio entre 1970 y el año actual
            val hora = random.nextInt(24) + 1
            // Genera un mes aleatorio (1-12)
            val minuto = random.nextInt(60) + 1

            horas.add("$hora:$minuto")
        }
        return horas
    }
}