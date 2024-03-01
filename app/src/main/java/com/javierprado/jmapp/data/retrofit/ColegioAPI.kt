package com.javierprado.jmapp.data.retrofit

import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.AuthResponse
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.entities.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Path;

interface ColegioAPI {
    // USUARIO
    @POST("/api/auth/login") // INICIAR SESION
    fun login(@Body usuario : Usuario, @Query("rol") rol : String): Call<AuthResponse?>?
    @POST("/api/auth/contrasena") // ACTUALIZAR CONTRASEÑA DE USUARIO
    fun actualizarContrasena(@Body usuario : Usuario): Call<String?>?
    @GET("/api/auth/sesion") // COMPROBAR SESION
    fun obtenerSesion(@Query("rol") rol : String): Call<Usuario>?

    //ADMIN
    @POST("/api/apoderados") // REGISTRA APODERADO Y TAMBIEN CREA CUENTA DE USUARIO
    fun agregarApoderado(@Body apoderado : Apoderado): Call<Int>

    // DOCENTE Y APODERADO
    @PUT("/api/docentes")
    fun actualizarInfoDocente(@Body docente: Docente): Call<String?>? // TAMBIEN ACTUALIZA INFO DE CUENTA
    @PUT("/api/apoderados")
    fun actualizarInfoApoderado(@Body apoderado : Apoderado): Call<String>? // TAMBIEN ACTUALIZA INFO DE CUENTA

    // ESTUDIANTES
    @GET("/api/estudiantes") // OBTENER ESTUDIANTES
    fun obtenerEstudiantes(@Query("curso") cursoId : Int?): Call<Collection<Estudiante>>?
    @GET("/api/estudiantes/{id}") // OBTENER ESTUDIANTES
    fun buscarEstudiantePorDNI(@Path("id")  estudianteId: Int, @Query("dni") dni : Int?): Call<Estudiante?>?
    // CURSOS
    @GET("/api/cursos") // OBTENER CURSOS
    fun obtenerCursos(@Query("estudiante") estudianteId : Int?): Call<Collection<Curso>?>?

    // NOTICIAS
    @GET("/api/noticias") // OBTENER NOTICIAS
    fun obtenerNoticias(): Call<List<Noticia>>?


}