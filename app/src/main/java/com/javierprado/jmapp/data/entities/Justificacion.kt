package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Justificacion:Serializable {
    var id: String = ""
    var asistenciaId: String = ""
    var apoderadoId: String = ""
    var motivoJustificacion: String = ""
    var motivoRechazo: String = ""
    var estado: String = ""
}