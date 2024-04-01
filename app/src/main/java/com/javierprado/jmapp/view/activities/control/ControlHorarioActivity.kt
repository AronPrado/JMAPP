package com.javierprado.jmapp.view.activities.control

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.fragments.HorarioFragment
import java.io.Serializable

class ControlHorarioActivity : AppCompatActivity() {
    val TOKEN = "token"
    val ROLE = "role"
    val ESTUDIANTES = "estudiantes"
    private var token = ""
    private var role = ""
    private var estudiantes : List<Estudiante> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_horario)

        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(TOKEN, "")
            role = bundle.getString(ROLE, "")
            val estudiantesP = bundle.getSerializable(ESTUDIANTES) as List<Estudiante>?
            if(estudiantesP != null){
                estudiantes =  estudiantesP
            }
        }
        // Botón regresar
        val backImageView: ImageView = findViewById(R.id.back)
        backImageView.setOnClickListener {
            val intent = getActivityRol(this@ControlHorarioActivity, role == RoleType.ADMIN.name)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        val fragment = HorarioFragment.newInstance(token, role == RoleType.ADMIN.name, estudiantes)
        supportFragmentManager.beginTransaction().replace(R.id.fcv_horario_main, fragment).addToBackStack("").commit()
    }
    fun getActivityRol(context: Context?, paraAdmin: Boolean): Intent{
        return Intent(context, if (paraAdmin) MenuAdministradorActivity::class.java else MenuApoderadoActivity::class.java)
    }
}