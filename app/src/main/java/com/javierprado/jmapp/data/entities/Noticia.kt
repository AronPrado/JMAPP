package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Noticia(): Serializable {
    var noticiaId: Int? = null
    var titulo: String? = null
    var contenido: String? = null
    var fechaPublicacion: String? = null
    var ubiImagen: String? = null

    lateinit var administrador: Administrador
    constructor(titulo: String, contenido: String, ubiImagen: String, administrador: Administrador) :this(){
        this.titulo=titulo
        this.contenido=contenido
        this.ubiImagen=ubiImagen
        this.administrador=administrador
    }
}