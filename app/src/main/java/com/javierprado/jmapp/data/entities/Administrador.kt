package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Administrador (
    val  administradorId: Int,
    val  nombres: String,
    val  apellidos: String,

    val  correo: String,
    val  telefono: Int,

    val  usuario: Usuario ,

    val  noticias: Collection<Noticia>,
    val  eventos: Collection<Evento>,
)