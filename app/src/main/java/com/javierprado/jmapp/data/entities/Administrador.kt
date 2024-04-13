package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Administrador: Serializable {
    var  id: String = ""
    var  nombres: String = ""
    var  apellidos: String =""

    var  correo: String = ""
    var  telefono: Int = 0
    var  usuarioId: String = ""
    var  noticias: List<String> = ArrayList()
    var  eventos: List<String> = ArrayList()
}