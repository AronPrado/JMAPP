package com.javierprado.jmapp.view.activities.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.notificaciones.FirebaseServiceReuniones
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.fragments.EstudiantesHFragment
import com.javierprado.jmapp.view.fragments.ProgramarReunionFragment
import java.io.Serializable

class ControlEstudianteActivity : AppCompatActivity() {
    val DIRECT = "direct" ; val AULAID = "aulaid"
    private var token = "" ; private var direct = "" ; private var apoderadoId = ""
    private var notificacion = false ; private lateinit var reunion: Reunion ; private var aulaId = ""
    var hijos: List<Estudiante> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_estudiante)

        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(MenuAdministradorActivity().TOKEN, "")
            apoderadoId = bundle.getString(MenuAdministradorActivity().USUARIOID, "")
            hijos = bundle.getSerializable(MenuApoderadoActivity().HIJOS) as List<Estudiante>
            direct = bundle.getString(DIRECT, NavigationWindows.FUNCIONES.name)

            notificacion = bundle.getBoolean(FirebaseServiceReuniones().NOTIFICACION_REUNIONES, false)
            reunion =  try{ bundle.getSerializable(ProgramarReunionFragment().REUNION) as Reunion }
            catch(c: NullPointerException){ Reunion() }
            aulaId = bundle.getString(ControlEstudianteActivity().AULAID, "")
        }
        // Botón regresar
//        val backImageView: ImageView = findViewById(R.id.back)
//        backImageView.setOnClickListener { finish() }

        val fragment = if(notificacion) {
            ProgramarReunionFragment.newInstance(token, reunion, apoderadoId, aulaId)
        }else{
            EstudiantesHFragment.newInstance(token, direct, apoderadoId, hijos as Serializable)
        }
        supportFragmentManager.beginTransaction().replace(R.id.fcv_apoderado_main, fragment).addToBackStack("").commit()
    }
}