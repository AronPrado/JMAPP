package com.javierprado.jmapp.data.entities

import androidx.room.util.TableInfo.*
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime


class Horario(): Serializable{
    var horarioId: Int = 0

    var fechaClase: String? = null
    var horaInicio: String? = null
    var horaFin: String? = null
    var curso: Curso = Curso()
}