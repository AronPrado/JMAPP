package com.javierprado.jmapp.data.entities

import java.io.Serializable
import java.time.LocalDate

data class Estudiante(
    var estudianteId: Int,
    var nombres: String,
    var apellidos: String,
    var fechaNacimiento: String,

    var genero: String,
    var dni: Int,

    var grado: Int,
    var seccion: String,
    var nivelEducativo: String,
    var fechaInscripcion: String,

    var estado: String,

    var itemsCurso: Set<Curso>,

    //    var calificaciones : Collection<Calificacion>,
//    var itemsAsistencia : Set<Asistencia>,
    var itemsApoderado: Set<Apoderado>,
)