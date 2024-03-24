package com.javierprado.jmapp.view.activities.comunicacion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.javierprado.jmapp.R
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
class ChatApoderadoDocenteActivity : AppCompatActivity() {
    val TOKEN = "token"
    private var token = ""
    private val TAG: String = toString()
    // LISTAS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_apoderado_docente)
        val bundle = intent.extras
        if (bundle != null) {
            token = bundle.getString(TOKEN, "")
            // LISTAS
        }
        // Botón regresar
//        val backImageView: ImageView = findViewById(R.id.back)
//        backImageView.setOnClickListener {
//            val intent = Intent(this@ChatApoderadoDocenteActivity,
//                MenuApoderadoActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(intent)
//        }
//        val fragment = ContenidoFragment.newInstance(token)
//        supportFragmentManager.beginTransaction().replace(R.id.fcv_docente_main,
//            fragment).addToBackStack(TAG).commit()
    }
}