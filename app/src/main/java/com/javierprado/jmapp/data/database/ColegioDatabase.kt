package com.javierprado.jmapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.javierprado.jmapp.data.database.dao.AdministradorDao
import com.javierprado.jmapp.data.database.dao.ApoderadosDao
import com.javierprado.jmapp.data.database.dao.DocentesDao
import com.javierprado.jmapp.data.database.dao.RolesDao
import com.javierprado.jmapp.data.database.dao.UsuariosDao
import com.javierprado.jmapp.data.entities.Administrador
import com.javierprado.jmapp.data.entities.Apoderado
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Rol_Usuario
import com.javierprado.jmapp.data.entities.Usuario

@Database(entities = [Usuario::class, Rol_Usuario::class, Docente::class, Apoderado::class, Administrador::class], version = 1)
abstract class ColegioDatabase:RoomDatabase() {

    abstract fun usuariosDao(): UsuariosDao
    abstract fun rolesDao(): RolesDao
    abstract fun docentesDao(): DocentesDao
    abstract fun apoderadosDao(): ApoderadosDao
    abstract fun administradorDao(): AdministradorDao

    companion object {
        @Volatile
        private var INSTANCE: ColegioDatabase? = null
        fun getDatabase(context: Context): ColegioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColegioDatabase::class.java,
                    "colegio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
