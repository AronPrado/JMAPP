package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

class Docente : Serializable {
    var id: String = ""
    var nombres: String = ""
    var apellidos: String = ""
    var genero: String = ""

    var correo: String = ""
    var telefono: Int = 0
    var direccion: String = ""

    var fechaNacimiento: String = ""
    var fechaRegistro: String = ""
    var cursoId: String = ""
    var usuarioId: String = ""
    var reuniones: List<String> = ArrayList()

    constructor()
    constructor(
        nombres: String,
        apellidos: String,
        genero: String,
        correo: String,
        telefono: Int,
        direccion: String,
        fechaNacimiento: String,
        cursoId: String
    ) {
        this.nombres = nombres
        this.apellidos = apellidos
        this.fechaNacimiento = fechaNacimiento
        this.genero = genero
        this.correo = correo
        this.telefono = telefono
        this.direccion = direccion
        this.cursoId = cursoId
    }
}
