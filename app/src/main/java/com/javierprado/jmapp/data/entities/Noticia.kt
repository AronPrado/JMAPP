package com.javierprado.jmapp.data.entities

import java.io.Serializable

data class Noticia (
    val noticiaId: Int,
    val titulo: String,
    val contenido: String,
    val fechaPublicacion: String,

    val administrador: Administrador?
)