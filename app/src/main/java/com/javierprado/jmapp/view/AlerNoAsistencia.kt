package com.javierprado.jmapp.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javierprado.jmapp.R
import com.javierprado.jmapp.databinding.ActivityAlerNoAsistenciaBinding

class AlerNoAsistencia : AppCompatActivity() {

    private lateinit var binding: ActivityAlerNoAsistenciaBinding

    private val TituloNombre = "Alerta"
    private val DescritionNombre = "Alerta"
    private val notificationId = 0
    private lateinit var mp: MediaPlayer
    private lateinit var btnSonido: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlerNoAsistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnSonido = findViewById(R.id.btn_alert_notification)
        mp = MediaPlayer.create(this, R.raw.timbredeescuela)

        btnSonido.setOnClickListener {
            mp.start()
            crearcanalNotificacion()
            crearNotificacion()
        }
    }

    private fun crearcanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canalImportancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(TituloNombre, DescritionNombre, canalImportancia)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }
    private fun crearNotificacion() {
        val notifacion = NotificationCompat.Builder(this, TituloNombre).also { it
            it.setContentTitle("Notificación")
            it.setContentText("Cuerpo de la notifiación")
            it.setSmallIcon(R.drawable.colegiologo)
            it.priority = NotificationCompat.PRIORITY_HIGH
        }.build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, notifacion)
    }
}