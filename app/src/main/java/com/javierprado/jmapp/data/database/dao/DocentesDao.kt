package com.javierprado.jmapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javierprado.jmapp.data.entities.Docente

@Dao
interface DocentesDao {

    @Query("SELECT * FROM docentes_table")
    suspend fun getAllDocentes():List<Docente>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(docentes:List<Docente>)
}