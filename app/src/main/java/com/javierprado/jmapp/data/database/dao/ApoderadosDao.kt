package com.javierprado.jmapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javierprado.jmapp.data.entities.Apoderados
@Dao
interface ApoderadosDao {

    @Query("SELECT * FROM apoderados_table")
    suspend fun getAllApoderados():List<Apoderados>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apoderados:List<Apoderados>)
}