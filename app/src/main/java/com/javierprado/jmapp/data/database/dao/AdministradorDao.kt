package com.javierprado.jmapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javierprado.jmapp.data.entities.Administrador
@Dao
interface AdministradorDao {
    @Query("SELECT * FROM administrador_table")
    suspend fun getAllAdministrador():List<Administrador>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(docentes:List<Administrador>)
}