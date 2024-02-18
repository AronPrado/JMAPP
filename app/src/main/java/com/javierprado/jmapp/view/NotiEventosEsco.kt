package com.javierprado.jmapp.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javierprado.jmapp.R

class NotiEventosEsco : AppCompatActivity() {

    private lateinit var tituloEvento: EditText
    private lateinit var descripcionEvento: EditText
    private lateinit var notificarEvento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noti_eventos_esco)

        tituloEvento = findViewById(R.id.tituloEvento)
        descripcionEvento = findViewById(R.id.descripcionEvento)
        notificarEvento = findViewById(R.id.notificarEvento)

        notificarEvento.setOnClickListener {
            val title = tituloEvento.text.toString()
            val description = descripcionEvento.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                showNotification(title, description)
            } else {
                Toast.makeText(applicationContext, "Por favor, ingrese el título y la descripción", Toast.LENGTH_SHORT).show()
            }
        }

        val mToolbar : Toolbar = findViewById(R.id.ToolbarP)
        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">" + "Eventos Escolares" + "</font>")
        mToolbar.setTitleTextAppearance(this, R.style.ToolbarTitleText)

        val titleTextView = mToolbar.getChildAt(0) as? TextView

        titleTextView?.gravity = Gravity.CENTER

        titleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23f)
    }

    private fun showNotification(title: String, description: String) {
        val builder = NotificationCompat.Builder(this, "eventChannel")
            .setSmallIcon(R.drawable.message)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, builder.build())
    }
}
