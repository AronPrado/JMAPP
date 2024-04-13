package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Reunion: Serializable{
    var id = ""
    var docenteId = ""
    var fecha = ""
    var horaInicio = ""
    var apoderadoId = ""
    var estudianteId = ""
    var estado = "RESPUESTA_D"
    constructor()
    constructor(fecha: String, horaInicio: String, docenteId: String, apoderadoId: String, estudianteId: String) : this() {
        this.fecha = fecha
        this.horaInicio = horaInicio
        this.docenteId = docenteId
        this.apoderadoId = apoderadoId
        this.estudianteId = estudianteId
    }
}