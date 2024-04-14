package com.javierprado.jmapp.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.view.activities.comunicacion.ChatApoderadoDocenteActivity
import com.javierprado.jmapp.view.activities.comunicacion.ChatDocenteApoderadoActivity
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class FirebaseServiceJustificaciones: FirebaseMessagingService() {
    companion object {
        private const val REPLY_ACTION_ID = "REPLY_ACTION_ID"
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"

        var sharedPref: SharedPreferences? = null

        var token: String?
            get() { return sharedPref?.getString("token", "") }
            set(value) { sharedPref?.edit()?.putString("token", value)?.apply() }
    }
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var intent = Intent()
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, error ->
            if (value != null && value.exists()) {
                val user = value.toObject(Users::class.java)!!
                val tipo = user.tipo
                if(tipo == RoleType.DOC.name){ intent =
                    Intent(this, ChatDocenteApoderadoActivity::class.java)
                }
                else if(tipo == RoleType.APOD.name){ intent =
                    Intent(this, ChatApoderadoDocenteActivity::class.java)
                }
            }
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        // for replies on notification
        val remoteInput = RemoteInput.Builder(KEY_REPLY_TEXT)
            .setLabel("Reply")
            .build()
        // Create a PendingIntent for the reply action
        val replyIntent = Intent(this, NotificationReply::class.java)
        val replyPendingIntent =
            PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)
        // Create a NotificationCompat.Action object for the reply action
        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.reply,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()
        val sharedCustomPref = SharedPrefs(applicationContext)
        sharedCustomPref.setIntValue("values", notificationID)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(message.data["title"])
            .setContentText(Html.fromHtml("<b>${message.data["title"]}</b>: ${message.data["message"]}"))
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .build()
        notificationManager.notify(notificationID, notification)
    }
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