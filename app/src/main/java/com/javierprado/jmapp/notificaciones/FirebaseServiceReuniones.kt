package com.javierprado.jmapp.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.fragments.ProgramarReunionFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


private const val CHANNEL_ID = "noti_reuniones"
class FirebaseServiceReuniones{
    val NOTIFICACION_REUNIONES= "noti_reu"
    val TIPO_NOTI = "REUNIONES"
    companion object {
        var sharedPref: SharedPreferences? = null
        val retro: RetrofitHelper = RetrofitHelper.getInstanceStatic()
        var token: String?
            get() { return sharedPref?.getString("token", "") }
            set(value) { sharedPref?.edit()?.putString("token", value)?.apply() }
    }
//    override fun onNewToken(newToken: String) {
//        super.onNewToken(newToken)
//        token = newToken
//    }
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        val data = message.data
//        if(message.data["tipo"] == TIPO_NOTI){
//        }
//    }
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}