package com.javierprado.jmapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(tableName = "usuarios_roles_table", primaryKeys = ["id_usuario", "rol_id"])
data class UsuarioRolUnion(
    @ColumnInfo(name = "id_usuario") val idUsuario: Int,
    @ColumnInfo(name = "rol_id") val rolId: Int
)

data class UsuarioConRoles(
    @Embedded val usuario: Usuario,
    @Relation(
        parentColumn = "id_usuario",
        entityColumn = "rol_id",
        associateBy = Junction(UsuarioRolUnion::class)
    )
    val roles: List<Rol_Usuario>
)

data class RolConUsuarios(
    @Embedded val rol: Rol_Usuario,
    @Relation(
        parentColumn = "rol_id",
        entityColumn = "id_usuario",
        associateBy = Junction(UsuarioRolUnion::class)
    )
    val usuarios: List<Usuario>
)

