package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.lang.reflect.Constructor

class Usuario : Serializable {
    var usuarioId: Int? = null
    var email: String? = null
    var contrasena: String? = null
    var telefono: Int? = null
    var nombres: String? = null
    var apellidos: String? = null
    var estado: String? = null

    lateinit var apoderado: Apoderado
    constructor() {}

    constructor(email: String, contrasena: String) : this() {
        this.email = email
        this.contrasena = contrasena
    }
}
