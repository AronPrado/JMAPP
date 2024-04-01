package com.javierprado.jmapp.notificaciones.network

import com.javierprado.jmapp.notificaciones.Constants.Companion.CONTENT_TYPE
import com.javierprado.jmapp.notificaciones.Constants.Companion.SERVER_KEY
import com.javierprado.jmapp.notificaciones.entities.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @Headers("Authorization:${SERVER_KEY}", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotificacion(@Body notification: PushNotification): Response<ResponseBody>

}