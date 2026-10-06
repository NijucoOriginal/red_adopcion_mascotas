package com.example.adopcion_mascotas.data.repository

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ReputacionDao {

    @Insert
    suspend fun insertarReputacion()
}