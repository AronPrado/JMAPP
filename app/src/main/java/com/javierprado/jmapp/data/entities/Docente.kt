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
    var telefono: String? = null
    var direccion: String? = null

    var fechaRegistro: String? = null
    var curso: Curso? = null
    var usuario: Usuario? = null
//    var mensajes : Collection<Mensaje>? = null
}
