package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

class Docente : Serializable {
    var docenteId: Int? = null
    var nombres: String? = null
    var apellidos: String? = null
    var fechaNacimiento: String? = null

    var genero: String? = null
    var correo: String? = null
    var telefono: Int? = null
    var direccion: String? = null

    var fechaRegistro: String? = null
    var curso: Curso? = null
    var usuario: Usuario? = null

    //    var mensajes : Collection<Mensaje>? = null
    constructor(
        nombres: String?,
        apellidos: String?,
        fechaNacimiento: String?,
        genero: String?,
        correo: String?,
        telefono: Int?,
        direccion: String?,
        curso: Curso?
    ) {
        this.nombres = nombres
        this.apellidos = apellidos
        this.fechaNacimiento = fechaNacimiento
        this.genero = genero
        this.correo = correo
        this.telefono = telefono
        this.direccion = direccion
        this.curso = curso
    }
}
