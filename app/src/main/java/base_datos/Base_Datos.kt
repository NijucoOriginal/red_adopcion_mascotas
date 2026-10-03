package base_datos

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dao.UsuarioDao
import domain.Usuario
import domain.Reputacion

@Database(entities = [Usuario::class,Reputacion::class], version = 2,exportSchema = false)
@TypeConverters(Conversor::class)
abstract class Base_Datos: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}