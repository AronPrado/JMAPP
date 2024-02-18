package com.javierprado.jmapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.javierprado.jmapp.R

class NotiTareasProyect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noti_tareas_proyect)

        val mToolbar : Toolbar = findViewById(R.id.ToolbarP)
        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">" + "Tareas y Proyectos de los Alumnos" + "</font>")

        val titleTextView = mToolbar.getChildAt(0) as? TextView
        titleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    }
}