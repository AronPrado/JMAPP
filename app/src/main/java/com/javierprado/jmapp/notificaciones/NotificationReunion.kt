package com.javierprado.jmapp.notificaciones

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.mvvm.ChatAppViewModel

private const val CHANNEL_ID = "my_channel"
class NotificationReunion: BroadcastReceiver() {
    val firestore = FirebaseFirestore.getInstance()
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager : NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val ap = intent?.extras?.getString("ACCION", "")
        Log.e("ACCION", ap?:"NO SE SELECCIONOXD")

        val sharedCustomPref = SharedPrefs(context)
        val notificationId : Int = sharedCustomPref.getIntValue("values", 0)
        val userAenviar = sharedCustomPref.getValue("d_notir") ?: ""
        val accionEnviadaACambiar = sharedCustomPref.getValue("a_notir") ?: "" // A_CANCELA , A_ACEPTA, D_ACEPTA
        val tipoRecibido = accionEnviadaACambiar.substring(0, 2)
        val tipoEnviar = if(tipoRecibido == "A_") "D_" else "A_"

        val accion = if (ap == "CANCELAR") tipoEnviar+"CANCELA" else tipoEnviar+"ACEPTA"
        val msg = if(ap == "CANCELAR") "Reunión cancelada." else "Reunión programada correctamente."

        val reunionId = sharedCustomPref.getValue("idreunion") ?: "null"
        val estado = if (ap == "CANCELAR") "CANCELADA" else "PROGRAMADA"

        //ENVIAR NOTI DE ACEPTACION O CANCELACION
        ChatAppViewModel().accionReuniones(AnotherUtil.getUidLoggedIn(),
            userAenviar, accion, Reunion(), true)

//        val hashMap = hashMapOf<String, Any>("estado" to estado)
//        firestore.collection("reuniones").document(reunionId).update(hashMap)


        val repliedNotification  =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.chatapp)//CAMBIAR
                .setContentText(msg).build()
        notificationManager.notify(notificationId, repliedNotification)
    }
}