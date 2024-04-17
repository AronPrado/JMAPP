package com.javierprado.jmapp.mvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.modal.Users
import com.javierprado.jmapp.notificaciones.FirebaseService
import com.javierprado.jmapp.notificaciones.entities.NotificacionDataReunion
import com.javierprado.jmapp.notificaciones.entities.PushNotificacion
import com.javierprado.jmapp.notificaciones.entities.Token
import com.javierprado.jmapp.notificaciones.network.RetrofitNoti
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotisApp: ViewModel(){
    val firestore = FirebaseFirestore.getInstance()
    var token: String? = null

    fun accionReuniones(sender: String, dataReceiver: String, accion: String, reunion: Reunion?, esid: Boolean = false)
    = viewModelScope.launch(Dispatchers.IO) {
        var receiver = "" ; var nombreSender = "" ; var nombreEstudiante = reunion?.estudianteId
        fun enviar(idReceiver: String){
            firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).addSnapshotListener { value, _ ->
                if (value != null && value.exists()) {
                    val user = value.toObject(Users::class.java)!! ; nombreSender = user.info!!
                    var mensaje = "" ; val fechaHora = reunion?.horaInicio+ " el "+reunion?.fecha
                    Log.e("ULTIMA NOTI", accion)
                    when(accion){
                        "A_PROGRAMA" -> mensaje = "Solicitud de reunión con el apoderado $nombreSender a las $fechaHora"
                        "D_ACEPTA" -> mensaje = "El docente $nombreSender ha aceptado la solicitud de programación de la reunión."
                        "D_REPROGRAMA" -> mensaje = "El docente $nombreSender quiere reprogramar la reunion a las $fechaHora"
                        "A_ACEPTA" -> mensaje = "El apoderado $nombreSender ha aceptado la solicitud de reprogramación."
                        "A_CANCELA" -> mensaje = "El apoderado $nombreSender ha cancelado la solicitud de reprogramación."
                    }
                    Log.e("ULTIMA NOTI", mensaje)
                    firestore.collection("Tokens").document(idReceiver).addSnapshotListener { value, _ ->
                        if (value != null && value.exists()) {
                            val tokenObject = value.toObject(Token::class.java)
                            token = tokenObject?.token!!
                            if (idReceiver.isNotEmpty()) {
                                PushNotificacion(
                                    NotificacionDataReunion("Reuniones", mensaje,
                                        FirebaseService().TIPOR, reunion?.id ?: "", accion, sender), token!!
                                ).also {
                                    sendNotification(it)
                                }
                            } else {
                                Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                            }
                        }
                        Log.e("ViewModel", token.toString())
                    }
                }
            }
        }
        if(!esid){
            //OBTENER INFO DEL USUARIO DESTINO
            val query = firestore.collection("Users").whereEqualTo("correo", dataReceiver)
            query.get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val user = querySnapshot.documents[0].toObject(Users::class.java)!!
                    enviar(user.userid!!)
                }
            }.addOnFailureListener { }
        }else{
            receiver = dataReceiver
            enviar(receiver)
        }
    }

    fun accionJustificaciones(){

    }

    private fun sendNotification(notification: PushNotificacion) = viewModelScope.launch{
        try {
            val response = RetrofitNoti.api.postNotificacion(notification)
        } catch (e: Exception) {
            Log.e("NotisApp", e.toString())
            // showToast(e.message.toString())
        }
    }
}