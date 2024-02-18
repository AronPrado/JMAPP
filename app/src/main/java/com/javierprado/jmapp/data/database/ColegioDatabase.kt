package com.javierprado.jmapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.javierprado.jmapp.data.database.dao.AdministradorDao
import com.javierprado.jmapp.data.database.dao.ApoderadosDao
import com.javierprado.jmapp.data.database.dao.DocentesDao
import com.javierprado.jmapp.data.database.dao.RolesDao
import com.javierprado.jmapp.data.database.dao.UsuariosDao
import com.javierprado.jmapp.data.entities.Administrador
import com.javierprado.jmapp.data.entities.Apoderados
import com.javierprado.jmapp.data.entities.Docentes
import com.javierprado.jmapp.data.entities.Roles
import com.javierprado.jmapp.data.entities.Usuarios

@Database(entities = [Usuarios::class, Roles::class, Docentes::class, Apoderados::class, Administrador::class], version = 1)
abstract class ColegioDatabase:RoomDatabase() {

    abstract fun usuariosDao(): UsuariosDao
    abstract fun rolesDao(): RolesDao
    abstract fun docentesDao(): DocentesDao
    abstract fun apoderadosDao(): ApoderadosDao
    abstract fun administradorDao(): AdministradorDao

}
