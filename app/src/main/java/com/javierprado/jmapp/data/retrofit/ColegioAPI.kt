package com.javierprado.jmapp.data.retrofit

import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.AuthResponse
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ColegioAPI {
    // USUARIO
    @POST("/api/auth/login")
    fun login(@Body usuario : Usuario): Call<AuthResponse?>?

    //ADMIN
    @POST("/api/apoderados")
    fun agregarApoderado(@Body apoderado : Apoderado): Call<AuthResponse?>? // TAMBIEN CREA CUENTA DE USUARIO

    // DOCENTE Y APODERADO
    @PUT("/api/docentes")
    fun actualizarInfoDocente(@Body docente: Docente): Call<AuthResponse?>? // TAMBIEN ACTUALIZA INFO DE CUENTA
    @PUT("/api/apoderados")
    fun actualizarInfoApoderado(@Body apoderado : Apoderado): Call<AuthResponse?>? // TAMBIEN ACTUALIZA INFO DE CUENTA

}