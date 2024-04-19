package com.javierprado.jmapp.view.activities.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.notificaciones.FirebaseService
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.fragments.EstudiantesHFragment
import com.javierprado.jmapp.view.fragments.JustificarFaltaFragment
import com.javierprado.jmapp.view.fragments.ProgramarReunionFragment
import java.io.Serializable

class ControlEstudianteActivity : AppCompatActivity() {
    val DIRECT = "direct" ; val AULAID = "aulaid"
    private var token = "" ; private var direct = "" ; private var apoderadoId = ""
    private var notificacion = false ; private lateinit var reunion: Reunion ; private var aulaId = ""
    private var justificacionId = "" ; var directNoti = false
    var hijos: List<Estudiante> = ArrayList() ; var tipoNotiParaApo = false ; var asistenciaId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_estudiante)

        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(MenuAdministradorActivity().TOKEN, "")
            apoderadoId = bundle.getString(MenuAdministradorActivity().USUARIOID, "")
            hijos = bundle.getSerializable(MenuApoderadoActivity().HIJOS) as List<Estudiante>
            direct = bundle.getString(DIRECT, NavigationWindows.FUNCIONES.name)
            directNoti = bundle.getBoolean(FirebaseService().TIPOJ)
            if(directNoti){
                justificacionId = bundle.getString(JustificarFaltaFragment().JUSTIFICACION, "")
                tipoNotiParaApo = bundle.getString("TIPONOTI_PARAAPODERADO", "")
                    .equals(FirebaseService().TIPOF)
                if(tipoNotiParaApo){
                    asistenciaId = bundle.getString("POR_FALTA", "")
                }
            }
        }
        // Botón regresar
//        val backImageView: ImageView = findViewById(R.id.back)
//        backImageView.setOnClickListener { finish() }
        var fragment: Fragment
        if(direct == NavigationWindows.JUSTIFICACIONES.name){
            fragment = JustificarFaltaFragment.newInstance(token, apoderadoId, justificacionId)
            if(tipoNotiParaApo){
                fragment = JustificarFaltaFragment.newInstance(token, apoderadoId, asistenciaId)
            }
//            if(directNoti){
//                fragment = JustificarFaltaFragment.newInstance(token, apoderadoId, justificacionId)
//            }
        }else{
            fragment = EstudiantesHFragment.newInstance(token, direct, apoderadoId, hijos as Serializable)
        }
        supportFragmentManager.beginTransaction().replace(R.id.fcv_apoderado_main, fragment).addToBackStack("").commit()
    }
}