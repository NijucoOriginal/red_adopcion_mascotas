package com.example.adopcion_mascotas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.adopcion_mascotas.data.repository.UsuarioDao
import com.example.adopcion_mascotas.domain.model.Usuario
import com.example.adopcion_mascotas.domain.model.Reputacion
import com.example.adopcion_mascotas.domain.model.Baneo
import com.example.adopcion_mascotas.domain.model.Comentario
import com.example.adopcion_mascotas.domain.model.Dosis
import com.example.adopcion_mascotas.domain.model.Foto
import com.example.adopcion_mascotas.domain.model.Foto_Carnet
import com.example.adopcion_mascotas.domain.model.Logro
import com.example.adopcion_mascotas.domain.model.Mascota
import com.example.adopcion_mascotas.domain.model.Mascota_Publicacion
import com.example.adopcion_mascotas.domain.model.Notificacion
import com.example.adopcion_mascotas.domain.model.Persona_Interesada
import com.example.adopcion_mascotas.domain.model.Persona_Que_Le_Gusta
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.model.Reputacion_Logro
import com.example.adopcion_mascotas.domain.model.Seguido
import com.example.adopcion_mascotas.domain.model.Vacuna


@Database(entities = [_root_ide_package_.com.example.adopcion_mascotas.domain.model.Baneo::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Comentario::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Dosis::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Foto::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Foto_Carnet::class,
                     _root_ide_package_.com.example.adopcion_mascotas.domain.model.Logro::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Mascota::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Mascota_Publicacion::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Notificacion::class,
                     _root_ide_package_.com.example.adopcion_mascotas.domain.model.Persona_Interesada::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Persona_Que_Le_Gusta::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Publicacion::class,
                     _root_ide_package_.com.example.adopcion_mascotas.domain.model.Reputacion_Logro::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Seguido::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class, _root_ide_package_.com.example.adopcion_mascotas.domain.model.Reputacion::class,
                     _root_ide_package_.com.example.adopcion_mascotas.domain.model.Vacuna::class], version = 3,exportSchema = false)
@TypeConverters(Conversor::class)
abstract class Base_Datos: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}