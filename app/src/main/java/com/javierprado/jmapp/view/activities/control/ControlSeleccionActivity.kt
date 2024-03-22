package com.javierprado.jmapp.view.activities.control

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
import com.javierprado.jmapp.view.fragments.SeleccionarAulaFragment

class ControlSeleccionActivity : AppCompatActivity() {
    val TOKEN = "token"
    private var token = ""
    private var direct = ""
    private val extraF = ExtraFunctions()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_seleccion)

        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(TOKEN, "")
            direct = bundle.getString(SeleccionarAulaFragment().DIRECT, NavigationWindows.FUNCIONES.name)

        }
        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = Intent(this@ControlSeleccionActivity, MenuDocenteActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        val fragment = SeleccionarAulaFragment.newInstance(token, direct)
        supportFragmentManager.beginTransaction().replace(R.id.fcv_docente_main, fragment).addToBackStack("").commit()
    }
}