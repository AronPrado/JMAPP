package com.javierprado.jmapp.view.activities.control

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.fragments.SeleccionarAulaFragment

class ControlSeleccionActivity : AppCompatActivity() {
    val DIRECT = "direct"
    private var token = "" ; private var direct = "" ; private var docenteId = "" ; private var cursoId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_seleccion)

        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(MenuAdministradorActivity().TOKEN, "")
            docenteId = bundle.getString(MenuAdministradorActivity().USUARIOID, "")
            cursoId = bundle.getString(MenuDocenteActivity().CURSOID, "")
            direct = bundle.getString(DIRECT, NavigationWindows.FUNCIONES.name)
        }
        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            finish()
//            val intent = Intent(this@ControlSeleccionActivity, MenuDocenteActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
        }
        val fragment = SeleccionarAulaFragment.newInstance(token, direct, docenteId, cursoId)
        supportFragmentManager.beginTransaction().replace(R.id.fcv_docente_main, fragment).addToBackStack("").commit()
    }
}