package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Administrador: Serializable {
    var  administradorId: Int = 0
    var  nombres: String = ""
    var  apellidos: String =""

    var  correo: String = ""
    var  telefono: Int = 0

    var  usuario: Usuario ? = null

    var  noticias: Collection<Noticia>? = null
    var  eventos: Collection<Evento>? = null
}