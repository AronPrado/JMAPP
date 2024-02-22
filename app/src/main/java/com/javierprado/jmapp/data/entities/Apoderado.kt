package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//@Entity(tableName = "apoderado_table")
data class Apoderado(
//    @PrimaryKey(autoGenerate = true)
    val apoderadoId: Int,

//    @ColumnInfo(name = "nombre")
    val nombres: String,

//    @ColumnInfo(name = "apellidos")
    val apellidos: String,

//    @ColumnInfo(name = "correo")
    val correo: String,

//    @ColumnInfo(name = "telefono")
    val telefono: Int,

//    @ColumnInfo(name = "direccion")
    val direccion: String?,

//    @ColumnInfo(name = "usuario_id")
    val usuario: Usuario,
)
