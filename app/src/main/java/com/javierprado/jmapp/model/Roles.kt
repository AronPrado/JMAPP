package com.javierprado.jmapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class Roles(
    @PrimaryKey(autoGenerate = true)
    val rol_id: Int,

    @ColumnInfo(name = "nombre_rol")
    val nombre_rol: String
)
