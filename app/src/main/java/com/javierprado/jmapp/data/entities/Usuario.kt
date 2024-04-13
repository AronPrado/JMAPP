package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Usuario : Serializable {
    var id: String = ""
    var correo: String = ""
    var contrasena: String = ""
    var telefono: Int = 0
    var nombres: String = ""
    var apellidos: String = ""
    var estado: String = ""
    var usuarioId: String = ""
    var roles: List<String> = ArrayList()
    var token: String = ""

    constructor()
    constructor(correo: String, contrasena: String, token: String) : this() {
        this.correo = correo
        this.contrasena = contrasena
        this.token = token
    }
}
