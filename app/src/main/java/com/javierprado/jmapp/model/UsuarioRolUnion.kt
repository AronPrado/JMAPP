package com.javierprado.jmapp.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(tableName = "usuarios_roles", primaryKeys = ["id_usuario", "rol_id"])
data class UsuarioRolUnion(
    @ColumnInfo(name = "id_usuario") val idUsuario: Int,
    @ColumnInfo(name = "rol_id") val rolId: Int
)

data class UsuarioConRoles(
    @Embedded val usuario: Usuarios,
    @Relation(
        parentColumn = "id_usuario",
        entityColumn = "rol_id",
        associateBy = Junction(UsuarioRolUnion::class)
    )
    val roles: List<Roles>
)

data class RolConUsuarios(
    @Embedded val rol: Roles,
    @Relation(
        parentColumn = "rol_id",
        entityColumn = "id_usuario",
        associateBy = Junction(UsuarioRolUnion::class)
    )
    val usuarios: List<Usuarios>
)

