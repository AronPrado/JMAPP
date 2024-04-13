package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Noticia(): Serializable {
    var id: String = ""
    var titulo: String = ""
    var contenido: String = ""
    var fechaPublicacion: String = ""
    var ubiImagen: String = ""
    var administradorId: String = ""

    constructor(titulo: String, contenido: String, ubiImagen: String, administradorId: String) :this(){
        this.titulo=titulo
        this.contenido=contenido
        this.ubiImagen=ubiImagen
        this.administradorId=administradorId
    }
}