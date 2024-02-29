package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Apoderado : Serializable {
    var apoderadoId: Int? = null
    var nombres: String? = null
    var apellidos: String? = null

    var correo: String? = null
    var telefono: Int? = null
    var direccion: String? = null

    var usuario: Usuario? = null
    constructor() {}
}