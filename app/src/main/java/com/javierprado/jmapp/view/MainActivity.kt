package com.javierprado.jmapp.view

import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        //Animaciones
        val animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba)
        val animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo)

        val welcomeView = findViewById<TextView>(R.id.welcomeView)
        val logoColegio = findViewById<ImageView>(R.id.logoColegio)

        welcomeView.animation = animacion2
        logoColegio.animation = animacion1
    }
}