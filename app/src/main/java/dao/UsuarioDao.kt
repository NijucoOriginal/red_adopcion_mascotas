package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import domain.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: Usuario)

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)


}