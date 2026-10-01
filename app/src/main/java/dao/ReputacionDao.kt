package dao

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ReputacionDao {

    @Insert
    suspend fun insertarReputacion()
}