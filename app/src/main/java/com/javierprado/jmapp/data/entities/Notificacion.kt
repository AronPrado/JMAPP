package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Notificacion(): Serializable {
    var id: String = ""
    var titulo: String = ""
    var contenido: String = ""
    var extra: String = ""
    var tipo: String = ""
    var usuarioId: String = ""
    var fecha: String = ""
    var hora: String = ""

    constructor(titulo: String, contenido: String, extra: String, usuarioId: String): this(){
        this.titulo=titulo
        this.contenido=contenido
        this.extra=extra
        this.usuarioId=usuarioId
    }
}