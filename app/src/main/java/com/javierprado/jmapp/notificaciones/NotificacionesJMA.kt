package com.javierprado.jmapp.notificaciones

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.notificaciones.entities.NotificacionData
import com.javierprado.jmapp.notificaciones.entities.NotificacionDataFalta
import com.javierprado.jmapp.notificaciones.entities.PushNotificacion
import com.javierprado.jmapp.notificaciones.entities.Token
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity

class NotificacionesJMA {
    companion object{
        val auth = FirebaseAuth.getInstance()
        @SuppressLint("StaticFieldLeak")
        val firestore = FirebaseFirestore.getInstance()

        private fun enviarPersonas(v: LifecycleOwner, emails: List<String>, msg: String, titulo: String, ids: List<String>){
            if(emails.isNotEmpty()){
                var users: List<Users> = ArrayList()
                ChatAppViewModel().getUsers(emails).observe(v) { users = it }
                firestore.collection("Users").addSnapshotListener { snapshot, exception ->
                    if (exception != null) { return@addSnapshotListener }
                    if(users.isNotEmpty()){
                        users.forEachIndexed { i,u ->
                            enviarNoticacion(u.userid!!, titulo, msg, ids[i])
                        }
//                        (context as ControlSeleccionActivity).supportFragmentManager.popBackStackImmediate()
                    } else{ Log.e("FAIL", "No users") }
                }
            }
        }
        fun notificarFalta(v: LifecycleOwner, emails: List<String>, curso: String, ids: List<String>){
            val titulo = "Informe"
            val msg = "El alumno faltó a la clase de $curso."
            enviarPersonas(v, emails, msg, titulo, ids)
        }
        private fun enviarNoticacion(destino: String, titulo: String, msg: String, asistenciaId: String){
            firestore.collection("Tokens").document(destino).addSnapshotListener { value, error ->
                if (value != null && value.exists()) {
                    val tokenObject = value.toObject(Token::class.java)
                    val token = tokenObject?.token!!
                    Log.e("TOKENS", token)
                    if (destino.isNotEmpty()) {
                        PushNotificacion(
                            NotificacionDataFalta(titulo, msg, FirebaseService().TIPOF, asistenciaId), token
                        ).also { ChatAppViewModel().sendNotification(it) }
                    } else { Log.e("FAIL", "NO TOKEN, NO NOTIFICATION") }
                }
            }
        }
    }
}