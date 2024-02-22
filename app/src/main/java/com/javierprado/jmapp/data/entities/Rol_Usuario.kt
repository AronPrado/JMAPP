package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles_table")
data class Rol_Usuario(
    @PrimaryKey(autoGenerate = true)
    val rol_id: Int,

    @ColumnInfo(name = "nombre_rol")
    val nombre_rol: String
)
