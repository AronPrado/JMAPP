package com.javierprado.jmapp.data.retrofit

import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Asistencia
import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Calificacion
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.entities.Justificacion
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.entities.Notificacion
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.entities.Tarea
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
    // USUARIOS
    @POST("/api/auth/login") // INICIAR SESION
    fun login(@Body usuario : Usuario, @Query("rol") rol : String): Call<Usuario>
    @PUT("/api/auth/contrasena") // ACTUALIZAR CONTRASEÑA DE USUARIO
    fun actualizarContrasena(@Body usuario : Usuario): Call<Void>//QUITAR
//    @GET("/api/auth/session") // COMPROBAR SESION
//    fun obtenerSesion(@Query("rol") rol : String): Call<Usuario>
    @GET("/api/auth/email") // COMPROBAR CORREO
    fun existeCorreo(@Query("correo") correo : String): Call<Void>

    // NOTICIAS
    @GET("/api/noticias") // OBTENER NOTICIAS
    fun listarNoticias(): Call<List<Noticia>>
    @GET("/api/noticias/{id}") // OBTENER NOTICIA POR ID
    fun buscarNoticia(@Path("id")  noticiaId: String): Call<Noticia>
    @POST("/api/noticias") // AGREGAR NOTICIA
    fun agregarNoticia(@Body noticia : Noticia) : Call<Void>
    @PUT("/api/noticias/{id}") // EDITAR NOTICIA
    fun editarNoticia(@Body noticia : Noticia, @Path("id")  noticiaId: String) : Call<Void>
    @DELETE("/api/noticias/{id}") // ELIMINAR NOTICIA
    fun eliminarNoticia(@Path("id")  noticiaId: String): Call<Void>

    // NOTIFICACIONES
    @GET("/api/notificaciones") // OBTENER NOTIFICACIONES
    fun listarNotificaciones(@Query("usuario")  usuarioId: String, @Query("notificacion")  notificacionId: String): Call<List<Notificacion>>
    @POST("/api/noticias") // AGREGAR NOTIFICACION
    fun agregarNotificacion(@Body noti : Notificacion) : Call<Void>
    @PUT("/api/noticias/{id}") // EDITAR NOTIFICACION
    fun editarNotificacion(@Path("id")  noticiaId: String, @Query("estado") estado: String) : Call<Void>
    @DELETE("/api/noticias/{id}") // ELIMINAR NOTIFICACION
    fun eliminarNotificacion(@Path("id")  notiId: String): Call<Void>

    // HORARIOS
    @POST("/api/horarios") // OBTENER HORARIOS
    fun listarHorarios(@Query("grado") grado : Int, @Query("nivel") nivel : String, @Query("seccion") seccion : String, @Query("fecha") fechaClase : String): Call<List<List<Horario>>>
    @GET("/api/horarios/{id}") // BUSCAR POR ID
    fun buscarHorario(@Path("id")  horarioId: String): Call<Horario>
    @PUT("/api/horarios") // ACTUALIZAR POR ID
    fun editarHorario(@Body horario: Horario, @Path("id")  horarioId: String): Call<Void>

    // ESTUDIANTES - AULA
    @GET("/api/estudiantes/aulas") // OBTENER AULAS
    fun listarAulas(@Query("docente") docenteId : String?, @Query("grado") grado : Int?, @Query("nivel") nivel : String?, @Query("seccion") seccion : String?): Call<List<Aula>>
    @POST("/api/estudiantes") // LISTAR
    fun listarEstudiantes(@Query("apoderado") apoderadoId : String, @Body estudiantes: List<String>): Call<List<Estudiante>>
    @GET("/api/estudiantes/{id}") // OBTENER ESTUDIANTES
    fun buscarEstudiante(@Path("id")  estudianteId: String?, @Query("dni") dni : Int?): Call<Estudiante>

    // CURSOS
    @GET("/api/cursos") // OBTENER CURSOS
    fun listarCursos(@Query("estudiante") estudianteId : String?, @Query("nivel") nivel : String?): Call<List<Curso>>
    @GET("/api/cursos/{id}") // OBTENER CURSO
    fun buscarCurso(@Query("id") cursoId : String?): Call<Curso>

    // ASISTENCIAS
    @POST("/api/asistencias") // OBTENER ASISTENCIAS
    fun listarAsistencias(@Body estudiantes: List<String>, @Query("fecha") fecha : String,
                          @Query("curso") cursoId: String, @Query("docente") docenteId: String): Call<List<Asistencia>>
    @PUT("/api/asistencias")
    fun editarAsistencias(@Body asistencias: List<Asistencia>): Call<List<String>>

    //CALIFICACIONES
    @POST("/api/calificaciones")
    fun listarCalificaciones(@Query("estudiante") estudianteId: String, @Query("curso") cursoId: String): Call<Calificacion>
    @PUT("/api/calificaciones/{id}")
    fun editarCalificaciones(@Body calificacion: Calificacion, @Path("id") calificacionId: String?): Call<Void>

    //TAREAS
    @GET("/api/tareas") // LISTAR
    fun listarTareas(@Query("aula") aulaId: String, @Query("docente") docenteId: String): Call<List<Tarea>>
    @POST("/api/tareas/{id}") // BUSCAR
    fun buscarTarea(@Path("id") tareaId: String): Call<Tarea>
    @POST("/api/tareas") // AGREGAR
    fun agregarTarea(@Body tarea : Tarea) : Call<Void>

    // DOCENTES
    @GET("/api/docentes") // LISTAR
    fun listarDocentes(@Query("curso") cursoId : String, @Query("estudiante") estudianteId : String, @Query("aula") aulaId: String): Call<List<Docente>>
    @GET("/api/docentes/{id}") // BUSCAR
    fun buscarDocente(@Path("id") docenteId: String): Call<Docente>
    @POST("/api/docentes/{id}") // REGISTRO y ACTUALIZACION
    fun guardarDocente(@Body docente: Docente, @Path("id") docenteId: String?): Call<Void>
    //    @PUT("/api/docentes")
    //    fun actualizarInfoDocente(@Body docente: Docente): Call<Void> // TAMBIEN ACTUALIZA INFO DE CUENTA

    //APODERADOS
    @GET("/api/apoderados") // LISTAR
    fun listarApoderados(@Query("estudiante") estudianteId: String): Call<List<Apoderado>>
    @GET("/api/apoderados/{id}") // BUSCAR
    fun buscarApoderado(@Path("id") apoderadoId: String): Call<Apoderado>
    @POST("/api/apoderados/reunion") // BUSCAR
    fun buscarApoderadoReunion(@Body reunion: Reunion): Call<Apoderado>
    @POST("/api/apoderados/{id}") // REGISTRO Y ACTUALIZACION
    fun guardarApoderado(@Body apoderado : Apoderado, @Path("id") apoderadoId: String?): Call<Void>
//    @PUT("/api/apoderados")
//    fun actualizarInfoApoderado(@Body apoderado : Apoderado): Call<Void> // TAMBIEN ACTUALIZA INFO DE CUENTA

    //REUNIONES
    @POST("/api/reuniones") // LISTAR
    fun listarReuniones(@Query("docente") docenteId: String, @Body reuniones: List<String>): Call<List<Reunion>>
    @GET("/api/reuniones/{id}") // BUSCAR
    fun buscarReunion(@Path("id") id: String): Call<Reunion>
    @POST("/api/reuniones/{id}") // GUARDAR - EDITAR
    fun guardarReunion(@Body reunion: Reunion, @Path("id") id: String): Call<Reunion>
    @DELETE("/api/reuniones/{id}") // ELIMINAR
    fun eliminarReunion(@Path("id") id: String): Call<Void>

    //JUSTIFICACIONES
    @GET("/api/justificaciones") // LISTAR
    fun listarJustificaciones(@Query("estudiante") estudianteId: String): Call<List<Justificacion>>
    @GET("/api/justificaciones/{id}") // BUSCAR
    fun buscarJustificacion(@Path("id") id: String): Call<Justificacion>
    @POST("/api/justificaciones/{id}") // GUARDAR - EDITAR
    fun guardarJustificacion(@Body justificacion: Justificacion, @Path("id") id: String): Call<Void>
    @DELETE("/api/justificaciones/{id}") // ELIMINAR
    fun eliminarJustificacion(@Path("id") id: String): Call<Void>
}