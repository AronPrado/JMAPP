@file:Suppress("DEPRECATION")

package com.javierprado.jmapp.notificaciones

import com.javierprado.jmapp.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
import androidx.core.app.RemoteInput
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.data.util.SharedPrefs
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.view.activities.comunicacion.ChatApoderadoDocenteActivity
import com.javierprado.jmapp.view.activities.comunicacion.ChatDocenteApoderadoActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.fragments.ProgramarReunionFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val CHANNEL_ID = "my_channel"
class FirebaseService: FirebaseMessagingService() {
    val TIPOM = "MENSAJES" ; val TIPOR = "REUNIONES" ; val TIPOJ = "JUSTIFICACIONES"
    companion object {
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"

        var sharedPref: SharedPreferences? = null
        val retro: RetrofitHelper = RetrofitHelper.getInstanceStatic()
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

        when(message.data["tipo"]){
            TIPOM -> { notiMensajes(message.data) }
            TIPOR -> { notiReuniones(message.data) }
            TIPOJ -> { notiJustificaciones(message.data) }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun notiReuniones(data: Map<String, String>){
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val accion = data["accion"]!! ; val userDestino = data["sender"]!!
        val tipo = if (accion.contains("D")) RoleType.APOD.name else RoleType.DOC.name
        val accionTipo = accion.split("_")[1][0].toString()// R, P, A, C
        val dcode = RoleType.DOC.name; val acode = RoleType.APOD.name
        //INTENT NOTIFICACION PARA EL DOCENTE
        var intent = if(tipo == dcode) { Intent(this@FirebaseService, ControlSeleccionActivity::class.java) } else{ Intent() }
        //INTENTS IZQUIERDA: A_ACEPTA - D_ACEPTA    NotificactionReunion
        val ia = Intent(this, NotificationReunion::class.java) ; val bundleI = Bundle()
        bundleI.putString("ACCION","ACEPTAR") ; ia.putExtras(bundleI)
//        ia.putExtra("ACCION", "ACEPTAR")
        val izquierdaPI = PendingIntent.getBroadcast(this@FirebaseService, 0, ia, PendingIntent.FLAG_IMMUTABLE)
        //INTENTS DERECHA: A_CANCELA - D_CANCELA - D_REPROGRAMA
        val ic = Intent(this, NotificationReunion::class.java) ; val bundleD = Bundle()
        bundleI.putString("ACCION", "CANCELAR") ; ic.putExtras(bundleI)
//        ic.putExtras("ACCION", "CANCELAR")
        var derechaPI = PendingIntent.getBroadcast(this@FirebaseService, 0, ic, PendingIntent.FLAG_IMMUTABLE)


        val bundle = Bundle()
        fun obtenerIntentBusqueda(intent: Intent, onResult: (Intent) -> Unit){
            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, _ ->
                if (value != null && value.exists()) {
                    val user = value.toObject(Users::class.java)!!
                    val tokenUser = user.token!!
                    retro.setBearerToken(tokenUser)

                    val reunionId = data["reunion"]!!
                    if(!reunionId.isEmpty()){
                        retro.getApi().buscarReunion(reunionId).enqueue(object: Callback<Reunion> {
                            override fun onResponse(call: Call<Reunion>, response: Response<Reunion>) {
                                val msg = response.headers()["message"] ?: ""
                                if (response.isSuccessful) {
                                    val reu = response.body()!!
                                    bundle.putSerializable(ProgramarReunionFragment().REUNION, reu)
                                    bundle.putString(MenuAdministradorActivity().TOKEN, tokenUser)
                                    bundle.putString(MenuAdministradorActivity().USUARIOID, user.tipoid)
                                    bundle.putBoolean(TIPOR, true)
                                    if(tipo == dcode){
                                        intent.putExtras(bundle);
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    //; intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    }
                                    onResult(intent)
                                }else{ Log.e("REUNION:", msg) }
                            }
                            override fun onFailure(call: Call<Reunion>, t: Throwable) {
                                Log.e("REUNION", t.message.toString())
                            }
                        } )
                    }
//                    onResult(intent)
                }
            }
        }
        obtenerIntentBusqueda(intent) { updatedIntent ->
            intent = updatedIntent

            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt() ; createNotificationChannel(notificationManager)

            // NotificationCompat.Action IZQUIERDA ACTION
            val izquierdaAction = NotificationCompat.Action.Builder(
                R.drawable.reply,//CAMBIAR
                "Aceptar", izquierdaPI ).build()

            if(tipo == dcode && accionTipo == "P"){
                derechaPI = PendingIntent.getActivity(this@FirebaseService, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            }
            // NotificationCompat.Action DERECHA action
            val derechaAction = NotificationCompat.Action.Builder(
                R.drawable.reply,//CAMBIAR
                if (accion == "A_PROGRAMA") "Reprogramar" else "Cancelar" , derechaPI).build()

            val sharedCustomPref = SharedPrefs(applicationContext)
            sharedCustomPref.setIntValue("values", notificationID)
            sharedCustomPref.setValue("d_notir", userDestino)
            sharedCustomPref.setValue("a_notir", accion)
            sharedCustomPref.setValue("idreunion", data["reunion"]!!)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(message.data["title"])
                .setContentText(Html.fromHtml("<b>${data["titulo"]}</b>: ${data["mensaje"]}"))
                .setSmallIcon(R.drawable.chatapp)//CAMBIAR ICONO DE REUNIONES
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            if(!listOf("A", "C").contains(accionTipo)) {  notification.addAction(izquierdaAction); notification.addAction(derechaAction) }
            notificationManager.notify(notificationID, notification.build())
        }
    }

    private fun notiJustificaciones(data: Map<String, String>){
//        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//        val accion = data["accion"]!! ; val userDestino = data["sender"]!!
//        val tipo = if (accion.contains("D")) RoleType.APOD.name else RoleType.DOC.name
//        val accionTipo = accion.split("_")[1][0].toString()// R, P, A, C
//        val dcode = RoleType.DOC.name; val acode = RoleType.APOD.name
//        //INTENT NOTIFICACION PARA EL DOCENTE
//        var intent = if(tipo == dcode) { Intent(this@FirebaseService, ControlSeleccionActivity::class.java) } else{ Intent() }
//        //INTENTS IZQUIERDA: A_ACEPTA - D_ACEPTA    NotificactionReunion
////        val ia = Intent(this, NotificationReunion::class.java) ; ia.putExtra("ACCION", "ACEPTAR")
////        val izquierdaPI = PendingIntent.getBroadcast(this@FirebaseService, 0, ia, PendingIntent.FLAG_MUTABLE)
//        //INTENTS DERECHA: A_CANCELA - D_CANCELA - D_REPROGRAMA
//        val ic = Intent(this, NotificationReunion::class.java) ; ic.putExtra("ACCION", "CANCELAR")
//        var derechaPI = PendingIntent.getBroadcast(this@FirebaseService, 0, ic, PendingIntent.FLAG_MUTABLE)
//
//        val bundle = Bundle()
//        fun obtenerIntentBusqueda(intent: Intent, onResult: (Intent) -> Unit){
//            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, _ ->
//                if (value != null && value.exists()) {
//                    val user = value.toObject(Users::class.java)!!
//                    val tokenUser = user.token!!
//                    retro.setBearerToken(tokenUser)
//
//                    val reunionId = data["reunion"]!!
//                    if(!reunionId.isEmpty()){
//                        retro.getApi().buscarReunion(reunionId).enqueue(object: Callback<Reunion> {
//                            override fun onResponse(call: Call<Reunion>, response: Response<Reunion>) {
//                                val msg = response.headers()["message"] ?: ""
//                                if (response.isSuccessful) {
//                                    val reu = response.body()!!
//                                    bundle.putSerializable(ProgramarReunionFragment().REUNION, reu)
//                                    bundle.putString(MenuAdministradorActivity().TOKEN, tokenUser)
//                                    bundle.putString(MenuAdministradorActivity().USUARIOID, user.tipoid)
//                                    bundle.putBoolean(TIPOR, true)
//                                    if(tipo == dcode){
//                                        intent.putExtras(bundle);
//                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                        //; intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                    }
//                                    onResult(intent)
//                                }else{ Log.e("REUNION:", msg) }
//                            }
//                            override fun onFailure(call: Call<Reunion>, t: Throwable) {
//                                Log.e("REUNION", t.message.toString())
//                            }
//                        } )
//                    }
////                    onResult(intent)
//                }
//            }
//        }
//        obtenerIntentBusqueda(intent) { updatedIntent ->
//            intent = updatedIntent
//
//            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            val notificationID = Random.nextInt() ; createNotificationChannel(notificationManager)
//
//            // NotificationCompat.Action IZQUIERDA ACTION
//            val izquierdaAction = NotificationCompat.Action.Builder(
//                R.drawable.reply,//CAMBIAR
//                "Aceptar", izquierdaPI ).build()
//            if(tipo == dcode && accionTipo == "P"){
//                derechaPI = PendingIntent.getActivity(this@FirebaseService, 0, intent,
//                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//            }
//            // NotificationCompat.Action DERECHA action
//            val derechaAction = NotificationCompat.Action.Builder(
//                R.drawable.reply,//CAMBIAR
//                if (accion == "A_PROGRAMA") "Reprogramar" else "Cancelar" , derechaPI).build()
//
//            val sharedCustomPref = SharedPrefs(applicationContext)
//            sharedCustomPref.setIntValue("values", notificationID)
//            sharedCustomPref.setValue("d_notir", userDestino)
//            sharedCustomPref.setValue("a_notir", accion)
//            sharedCustomPref.setValue("idreunion", data["reunion"]!!)
//
//            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
////            .setContentTitle(message.data["title"])
//                .setContentText(Html.fromHtml("<b>${data["titulo"]}</b>: ${data["mensaje"]}"))
//                .setSmallIcon(R.drawable.chatapp)//CAMBIAR ICONO DE REUNIONES
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//            if(!listOf("A", "C").contains(accionTipo)) {  notification.addAction(izquierdaAction); notification.addAction(derechaAction) }
//            notificationManager.notify(notificationID, notification.build())
//        }
    }

    private fun notiMensajes(data: Map<String, String>){
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var intent = Intent()
        firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, error ->
            if (value != null && value.exists()) {
                val user = value.toObject(Users::class.java)!!
                val tipo = user.tipo
                if(tipo == RoleType.DOC.name){ intent = Intent(this, ChatDocenteApoderadoActivity::class.java) }
                else if(tipo == RoleType.APOD.name){ intent = Intent(this, ChatApoderadoDocenteActivity::class.java) }
            }
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        val replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)
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
            .setContentText(Html.fromHtml("<b>${data["titulo"]}</b>: ${data["mensaje"]}"))
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .build()
        notificationManager.notify(notificationID, notification)
    }
}