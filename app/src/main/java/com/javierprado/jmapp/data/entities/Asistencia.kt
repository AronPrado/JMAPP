package com.javierprado.jmapp.data.entities

import java.io.Serializable
import java.time.LocalDate
class Asistencia(): Serializable {
    var asistenciaId: Int? = null
    var estado: String = ""
    var fecha: String = ""
    var justificacion: String = ""
    var itemsEstudiante: Set<Estudiante>  = HashSet()
}