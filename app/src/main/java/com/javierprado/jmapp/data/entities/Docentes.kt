package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "docentes_table")
data class Docentes(
    @PrimaryKey(autoGenerate = true)
    val id_docente: Int,

    @ColumnInfo(name = "id_usuario") //Referencia al id del usuario
    val id_usuario: Int,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellidos")
    val apellidos: String,

    @ColumnInfo(name = "correo_electronico")
    val correo_electronico: String,

    @ColumnInfo(name = "telefono")
    val telefono: String,

    @ColumnInfo(name = "fecha_nacimiento")
    val fecha_nacimiento: String,

    @ColumnInfo(name = "genero")
    val genero: String,

    @ColumnInfo(name = "especialidad")
    val especialidad: String,

    @ColumnInfo(name = "direccion")
    val direccion: String,

    @ColumnInfo(name = "fecha_contratacion")
    val fecha_contratacion: String

)
