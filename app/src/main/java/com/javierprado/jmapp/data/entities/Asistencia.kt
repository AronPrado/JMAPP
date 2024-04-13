package com.javierprado.jmapp.data.entities

import java.io.Serializable
class Asistencia: Serializable {
    var id: String = ""
    var estado: String = ""
    var fecha: String = ""
    var estudianteId: String = ""
    var cursoId: String = ""
    var docenteId: String = ""
}