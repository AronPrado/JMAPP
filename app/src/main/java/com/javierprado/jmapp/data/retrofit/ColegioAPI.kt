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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Path;

interface ColegioAPI {
    // USUARIO
    @POST("/api/auth/login") // INICIAR SESION
    fun login(@Body usuario : Usuario, @Query("rol") rol : String): Call<AuthResponse?>?
    @PUT("/api/auth/contrasena") // ACTUALIZAR CONTRASEÑA DE USUARIO
    fun actualizarContrasena(@Body usuario : Usuario): Call<Void>?
    @GET("/api/auth/session") // COMPROBAR SESION
    fun obtenerSesion(@Query("rol") rol : String): Call<Usuario>
    @GET("/api/auth/email") // COMPROBAR CORREO
    fun existeCorreo(@Query("correo") correo : String): Call<Void>?

    //ADMIN
    @POST("/api/docentes") // REGISTRO APODERADO Y USUARIO DE DOCENTE NEW
    fun agregarDocente(@Body docente : Docente): Call<Docente>
    @POST("/api/apoderados") // REGISTRO APODERADO Y USUARIO DE APODERADO
    fun agregarApoderado(@Body apoderado : Apoderado): Call<Int>
    @POST("/api/noticias") // AGREGAR NOTICIA NEW
    fun agregarNoticia(@Body noticia : Noticia) : Call<Noticia>
    @PUT("/api/noticias") // EDITAR NOTICIA NEW
    fun editarNoticiaPorId(@Body noticia : Noticia) : Call<Void>
    @DELETE("/api/noticias/{id}") // ELIMINAR NOTICIA NEW
    fun eliminarNoticiaPorId(@Path("id")  noticiaId: Int): Call<Void>

    // DOCENTE Y APODERADO
    @PUT("/api/docentes")
    fun actualizarInfoDocente(@Body docente: Docente): Call<Void>? // TAMBIEN ACTUALIZA INFO DE CUENTA
    @PUT("/api/apoderados")
    fun actualizarInfoApoderado(@Body apoderado : Apoderado): Call<Void>? // TAMBIEN ACTUALIZA INFO DE CUENTA

    // ESTUDIANTES
    @GET("/api/estudiantes") // OBTENER ESTUDIANTES
    fun obtenerEstudiantes(@Query("curso") cursoId : Int?, @Query("grado") grado : Int?, @Query("seccion") seccion : String?): Call<Collection<Estudiante>>?
    @GET("/api/estudiantes/{id}") // OBTENER ESTUDIANTES
    fun buscarEstudiantePorDNI(@Path("id")  estudianteId: Int, @Query("dni") dni : Int?): Call<Estudiante>?

    // CURSOS
    @GET("/api/cursos") // OBTENER CURSOS
    fun obtenerCursos(@Query("estudiante") estudianteId : Int?, @Query("nivel") nivel : String?): Call<Collection<Curso>>?

    // NOTICIAS
    @GET("/api/noticias") // OBTENER NOTICIAS
    fun obtenerNoticias(): Call<List<Noticia>>?
    @GET("/api/noticias/{id}") // OBTENER NOTICIA POR ID NEW
    fun obtenerNoticiaPorId(@Path("id")  noticiaId: Int): Call<Noticia>


}