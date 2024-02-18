package com.javierprado.jmapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuarios(
    @PrimaryKey(autoGenerate = true)
    val id_usuario: Int,

    @ColumnInfo(name = "nombre")
    val nombre:String,

    @ColumnInfo(name = "apellidos")
    val apellidos:String,

    @ColumnInfo(name = "contraseña")
    val contraseña:String,

    @ColumnInfo(name = "telefono")
    val telefono : String
)
