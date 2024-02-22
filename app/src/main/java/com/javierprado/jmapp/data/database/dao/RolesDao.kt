package com.javierprado.jmapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javierprado.jmapp.data.entities.Rol_Usuario

@Dao
interface RolesDao {
    @Query("SELECT * FROM roles_table")
    suspend fun getAllRoles():List<Rol_Usuario>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roles:List<Rol_Usuario>)
}