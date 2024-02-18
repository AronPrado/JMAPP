package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "administrador_table")
data class Administrador(
    @PrimaryKey(autoGenerate = true)
    val id_admin: Int,

    @ColumnInfo(name = "id_usuario") //Referencia al id del usuario
    val id_usuario: Int,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellidos")
    val apellidos: String,

    @ColumnInfo(name = "correo_electronico")
    val correo_electronico: String,

    @ColumnInfo(name = "telefono")
    val telefono: String
)
