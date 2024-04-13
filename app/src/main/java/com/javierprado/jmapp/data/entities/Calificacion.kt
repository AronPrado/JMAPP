package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Calificacion : Serializable {
    var id: String = ""
    var calificacion1: Int = 0
    var calificacion2: Int = 0
    var calificacion3: Int = 0
    var calificacion4: Int = 0

    var calificacionFinal: Double = 0.0

    var cursoId: String = ""
    var estudianteId: String = ""
}