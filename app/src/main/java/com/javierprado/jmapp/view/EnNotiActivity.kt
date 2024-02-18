package com.javierprado.jmapp.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javierprado.jmapp.R
import com.javierprado.jmapp.databinding.NotificacionSituacionBinding

class EnNotiActivity : AppCompatActivity() {

    private lateinit var binding: NotificacionSituacionBinding

    private val canalNombre = "App"
    private val canalId = "canalId"
    private val notificacionId = 0

    private lateinit var yourCustomSoundUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NotificacionSituacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mToolbar : Toolbar = findViewById(R.id.ToolbarP)
        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">" + "¡Aviso, el alumno no se encuentra en clases!" + "</font>")


        val titleTextView = mToolbar.getChildAt(0) as? TextView
        titleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)

        binding.buttonNoti.setOnClickListener {
            crearCanalNotificacion()
            crearNotificacion()
        }
    }

    private fun crearCanalNotificacion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val canalImportancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(canalId, canalNombre, canalImportancia)

            canal.setSound(yourCustomSoundUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
            canal.setShowBadge(true)
            canal.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            canal.enableLights(true)
            canal.enableVibration(true)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)

        }
    }

    private fun crearNotificacion(){
        val resultIntent = Intent(applicationContext, Justificacion::class.java)

        val resultPendingIntent = TaskStackBuilder.create(applicationContext).run{
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notificacion = NotificationCompat.Builder(this, canalId).also{
            it.setContentTitle("¡AVISO!")
            it.setContentText("Su hijo no se encuentra en el aula, Justifique su ausencia o pongase en contacto con nosotros")
            it.setSmallIcon(R.drawable.message)
            it.priority = NotificationCompat.PRIORITY_HIGH
            it.setContentIntent(resultPendingIntent)
            it.setAutoCancel(true)
            it.setSound(yourCustomSoundUri)
            it.setVibrate(longArrayOf(0, 1000, 1000, 1000))
            it.setOngoing(true)
        }.build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificacionId, notificacion)
    }
}