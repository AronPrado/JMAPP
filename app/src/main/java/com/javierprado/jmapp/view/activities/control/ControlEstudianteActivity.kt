package com.javierprado.jmapp.view.activities.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.fragments.EstudiantesHFragment
import java.io.Serializable

class ControlEstudianteActivity : AppCompatActivity() {
    val DIRECT = "direct" ; val AULAID = "aulaid"
    private var token = "" ; private var direct = "" ; private var apoderadoId = ""
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
        }
        // Botón regresar
//        val backImageView: ImageView = findViewById(R.id.back)
//        backImageView.setOnClickListener { finish() }

        val fragment = EstudiantesHFragment.newInstance(token, direct, apoderadoId, hijos as Serializable)
        supportFragmentManager.beginTransaction().replace(R.id.fcv_apoderado_main, fragment).addToBackStack("").commit()
    }
}