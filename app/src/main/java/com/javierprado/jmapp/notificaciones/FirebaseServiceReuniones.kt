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
class FirebaseServiceReuniones {//: FirebaseMessagingService()
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
//        if(data["tipo"] == TIPO_NOTI){
//            val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//            val accion = data["accion"]!! ; val userDestino = data["sender"]!!
//
//            var intent = Intent() ; val dcode = RoleType.DOC.name; val acode = RoleType.APOD.name
//            val bundle = Bundle()
//            var tipo = ""
//
//            val intentUsuario = hashMapOf(dcode to ControlSeleccionActivity::class.java,
//                acode to ControlEstudianteActivity::class.java)
//            val intentAccionUsuario = hashMapOf(dcode to ControlSeleccionActivity::class.java,
//                acode to NotificationReunion::class.java)
//            val intentCancelar = Intent(this, intentAccionUsuario[acode]) ; intentCancelar.putExtra("ACCION", "CANCELADA")
//            val pendingsTOActions = hashMapOf(
//                dcode to PendingIntent.getActivity(this, 0, Intent(this, intentAccionUsuario[dcode]), PendingIntent.FLAG_MUTABLE),
//                acode to PendingIntent.getBroadcast(this, 0, intentCancelar, PendingIntent.FLAG_MUTABLE)
//            )
//
//            //OBTENER INFO DEL USUARIO
//            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, error ->
//                if (value != null && value.exists()) {
//                    val user = value.toObject(Users::class.java)!!
//                    tipo = user.tipo!!
//                    val tokenUser = user.token!!
//                    retro.setBearerToken(tokenUser)
//                    intent = Intent(this, intentUsuario[tipo])
//                    val reunionId = data[ProgramarReunionFragment().IDREUNION]!!
//                    retro.getApi().buscarReunion(reunionId).enqueue(object : Callback<Reunion> {
//                        override fun onResponse(call: Call<Reunion>, response: Response<Reunion>) {
//                            val msg = response.headers()["message"] ?: ""
//                            if (response.isSuccessful) {
//                                val reu = response.body()!!
//                                bundle.putSerializable(ProgramarReunionFragment().REUNION, reu)
//                                bundle.putString(MenuAdministradorActivity().TOKEN, tokenUser)
//                                bundle.putString(MenuAdministradorActivity().USUARIOID, user.tipoid)
//                            }else{ Log.e("REUNION:", msg) }
//                        }
//                        override fun onFailure(call: Call<Reunion>, t: Throwable) {
//                            Log.e("REUNION", t.message.toString())
//                        }
//                    } )
//                }
//            }
//
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            val notificationID = Random.nextInt()
//
//            createNotificationChannel(notificationManager)
//
//            intent.putExtras(bundle)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//
//            // PendingIntent para APROBAR_REUNION
//            val aprobarIntent = Intent(this, NotificationReunion::class.java)//CAMBIAR
//            aprobarIntent.putExtra("ACCION", "ACEPTADA_"+if(tipo == acode) "A" else "D")
//            val aprobarPendingIntent =
//                PendingIntent.getBroadcast(this, 0, aprobarIntent, PendingIntent.FLAG_MUTABLE)
//            // NotificationCompat.Action APROBAR_REUNION action
//            val aprobarAction = NotificationCompat.Action.Builder(
//                R.drawable.reply,//CAMBIAR
//                "Aceptar", aprobarPendingIntent ).build()
//
//            // PendingIntent para REPROGRAMAR o CANCELAR
//            val repOcanPendingIntent = pendingsTOActions[tipo]
//            // NotificationCompat.Action REPROGRAMAR o CANCELAR action
//            val repOcanAction = NotificationCompat.Action.Builder(
//                R.drawable.reply,//CAMBIAR
//                accion, repOcanPendingIntent ).build()
//
//            val sharedCustomPref = SharedPrefs(applicationContext)
//            sharedCustomPref.setIntValue("values", notificationID)
//            sharedCustomPref.setValue("d_notir", userDestino) // USER para notificar accion
////            sharedCustomPref.setValue("nd_notir", nombreDestino)
////            sharedCustomPref.setValue("en_notir", estudianteNombre)
//            sharedCustomPref.setValue("td_notir", tipo)
//
//            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
////            .setContentTitle(message.data["title"])
//                .setContentText(Html.fromHtml("<b>${data["title"]}</b>: ${data["message"]}"))
//                .setSmallIcon(R.drawable.chatapp)//CAMBIAR ICONO DE REUNIONES
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .addAction(aprobarAction)
//                .addAction(repOcanAction)
//                .build()
//            notificationManager.notify(notificationID, notification)
//        }
//    }
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "channelName"
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "My channel description"
//            enableLights(true)
//            lightColor = Color.GREEN
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
}