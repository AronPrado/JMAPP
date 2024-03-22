package com.javierprado.jmapp.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBindings
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.clases.NewsAdapter
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.view.fragments.AsignarTareasFragment
import com.javierprado.jmapp.view.fragments.EstudiantesConFuncionesFragment
import com.javierprado.jmapp.view.fragments.IngresarCalificacionesFragment
import com.javierprado.jmapp.view.fragments.RegistroAsistenciaFragment
import com.javierprado.jmapp.view.fragments.SeleccionarAulaFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class ExtraFunctions {
    fun listarNoticias(api: ColegioAPI, adapter: NewsAdapter, context: Context){
        var noticias : List<Noticia>
        var msg : String

        val progressBarListar: CircularProgressIndicator = (context as AppCompatActivity).findViewById(R.id.pb_listar_noticias)

        progressBarListar.visibility= View.VISIBLE
        api.obtenerNoticias()?.enqueue(object : Callback<List<Noticia>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<Noticia>>, response: Response<List<Noticia>>) {
                if (response.isSuccessful) {
                    noticias = response.body()!!
                    adapter.setNoticias(noticias)
                    adapter.notifyDataSetChanged()
                    progressBarListar.visibility= View.GONE
                    if(noticias.isEmpty()){
                        msg = "No hay noticias."
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<Noticia>>, t: Throwable) {
                msg = "Error en la API"
                Log.e(msg, t.message.toString())
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                progressBarListar.visibility= View.GONE
            }
        })
    }

    fun obtenerFragment(nav: String, token: String, lista: Serializable ): Fragment{
        var fragment = Fragment()
        when(nav){
            NavigationWindows.SELECT.name-> { fragment = SeleccionarAulaFragment.newInstance(token, nav) }
            NavigationWindows.FUNCIONES.name-> { fragment = EstudiantesConFuncionesFragment.newInstance(token, lista) }
            NavigationWindows.ASISTENCIAS.name-> { fragment = RegistroAsistenciaFragment.newInstance(token, lista) }
//            NavigationWindows.NOTAS.name-> { fragment = IngresarCalificacionesFragment.newInstance(token, estudianteId) }
            NavigationWindows.TAREAS.name-> { fragment = AsignarTareasFragment.newInstance(token, lista) }

        }
        return fragment
    }
}