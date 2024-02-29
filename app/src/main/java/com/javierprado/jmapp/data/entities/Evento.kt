package com.javierprado.jmapp.data.entities

import java.io.Serializable

class Evento : Serializable {
    var eventoId: Int? = null
    var titulo: String? = null
    var descripcion: String? = null
    var fecha: String? = null

    var administrador: Administrador? = null
    constructor() {}
}