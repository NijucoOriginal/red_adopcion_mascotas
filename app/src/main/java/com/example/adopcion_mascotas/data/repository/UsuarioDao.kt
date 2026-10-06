package com.example.adopcion_mascotas.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.example.adopcion_mascotas.domain.model.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: com.example.adopcion_mascotas.domain.model.Usuario)

    @Update
    suspend fun actualizarUsuario(usuario: com.example.adopcion_mascotas.domain.model.Usuario)


}