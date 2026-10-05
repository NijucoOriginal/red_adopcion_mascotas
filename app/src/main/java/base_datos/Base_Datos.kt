package base_datos

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dao.UsuarioDao
import domain.Usuario
import domain.Reputacion
import domain.Baneo
import domain.Comentario
import domain.Dosis
import domain.Foto
import domain.Foto_Carnet
import domain.Logro
import domain.Mascota
import domain.Mascota_Publicacion
import domain.Notificacion
import domain.Persona_Interesada
import domain.Persona_Que_Le_Gusta
import domain.Publicacion
import domain.Reputacion_Logro
import domain.Seguido
import domain.Vacuna


@Database(entities = [Baneo::class,Comentario::class,Dosis::class,Foto::class,Foto_Carnet::class,
                     Logro::class,Mascota::class, Mascota_Publicacion::class,Notificacion::class,
                     Persona_Interesada::class,Persona_Que_Le_Gusta::class,Publicacion::class,
                     Reputacion_Logro::class,Seguido::class,Usuario::class,Reputacion::class,
                     Vacuna::class], version = 3,exportSchema = false)
@TypeConverters(Conversor::class)
abstract class Base_Datos: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}