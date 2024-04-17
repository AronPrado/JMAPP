package com.javierprado.jmapp.view.activities.notificaciones

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.view.fragments.RegistroDeNotificacionFragment

class RegistroDeNotificacionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_de_notificacion)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_rnotificaciones_main, RegistroDeNotificacionFragment())
            .commit()

    }
}