package com.javierprado.jmapp.data.entities

import java.io.Serializable
import java.time.LocalDate

class Estudiante: Serializable{
    var estudianteId: Int = 0
    var nombres: String?= null
    var apellidos: String?= null
    var fechaNacimiento: String?= null

    var genero: String?= null
    var dni: Int = 0

    var grado: Int?= null
    var seccion: String?= null
    var nivelEducativo: String?= null
    var fechaInscripcion: String?= null

    var estado: String?= null

    var itemsCurso: Set<Curso> = HashSet()
//        var calificaciones : Collection<Calificacion>?= null
//    var itemsAsistencia : Set<Asistencia>?= null
//    var itemsApoderado: Set<Apoderado>?= null
}