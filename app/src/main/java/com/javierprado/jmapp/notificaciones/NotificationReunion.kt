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
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.view.fragments.ProgramarReunionFragment

private const val CHANNEL_ID = "my_channel"
class NotificationReunion: BroadcastReceiver() {
    val firestore = FirebaseFirestore.getInstance()
    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager : NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val accion = intent?.extras?.getString("ACCION") ?: ""
        val reunion = try { intent?.extras?.getSerializable(ProgramarReunionFragment().REUNION) as Reunion }
        catch (e: NullPointerException){ Reunion() }

        val sharedCustomPref = SharedPrefs(context)
        val replyid : Int = sharedCustomPref.getIntValue("values", 0)
        val userAenviar = sharedCustomPref.getValue("d_notir") ?: ""

        var msg = "Su solicitud de reunión con el docente -- ha sido aceptada."
        //ENVIAR NOTI DE ACEPTACION O CANCELACION
        ChatAppViewModel().accionReuniones(AnotherUtil.getUidLoggedIn(),
            userAenviar, accion, reunion)

        msg = if(accion == "CANCELAR") "Reunión cancelada." else "Reunión programada correctamente."
        val repliedNotification  =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.chatapp)//CAMBIAR
                .setContentText(msg).build()
        notificationManager.notify(replyid, repliedNotification)
    }
}