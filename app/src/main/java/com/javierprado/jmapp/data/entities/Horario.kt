package com.javierprado.jmapp.data.entities

import java.io.Serializable


class Horario(): Serializable{
    var id: String = ""

    var fechaClase: String = ""
    var horaInicio: String = ""
    var horaFin: String = ""
    var cursoId: String = ""
    var docenteId: String = ""
}