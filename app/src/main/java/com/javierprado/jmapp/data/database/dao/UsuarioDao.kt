package com.javierprado.jmapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javierprado.jmapp.data.entities.Usuario
@Dao
interface UsuariosDao {
    @Query("SELECT * FROM usuario_table")
    suspend fun getAllUsuarios():List<Usuario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usuarios:List<Usuario>)
}