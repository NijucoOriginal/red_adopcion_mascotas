package com.example.adopcion_mascotas.data.local

import androidx.room.TypeConverter
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoBaneo
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoUsuario
import com.example.adopcion_mascotas.domain.model.Rol
import com.example.adopcion_mascotas.domain.model.TipoAnimal
import java.time.LocalDateTime

class Conversor {

    @TypeConverter
    fun fromEstadoUsuario(value: com.example.adopcion_mascotas.domain.model.EstadoUsuario): String=value.toString()

    @TypeConverter
    fun toEstadoUsuario(value: String): com.example.adopcion_mascotas.domain.model.EstadoUsuario = _root_ide_package_.com.example.adopcion_mascotas.domain.model.EstadoUsuario.valueOf(value)

    @TypeConverter
    fun fromEstadoBaneo(value: com.example.adopcion_mascotas.domain.model.EstadoBaneo): String=value.toString()

    @TypeConverter
    fun toEstadoBaneo(value: String): com.example.adopcion_mascotas.domain.model.EstadoBaneo = _root_ide_package_.com.example.adopcion_mascotas.domain.model.EstadoBaneo.valueOf(value)

    @TypeConverter
    fun fromTipoAnimal(value: com.example.adopcion_mascotas.domain.model.TipoAnimal): String=value.toString()

    @TypeConverter
    fun toTipoAnimal(value: String): com.example.adopcion_mascotas.domain.model.TipoAnimal = _root_ide_package_.com.example.adopcion_mascotas.domain.model.TipoAnimal.valueOf(value)

    @TypeConverter
    fun fromCategoriaPublicacion(value: com.example.adopcion_mascotas.domain.model.CategoriaPublicacion): String=value.toString()

    @TypeConverter
    fun toCategoriaPublicacion(value: String): com.example.adopcion_mascotas.domain.model.CategoriaPublicacion = _root_ide_package_.com.example.adopcion_mascotas.domain.model.CategoriaPublicacion.valueOf(value)

    @TypeConverter
    fun fromEstadoPublicacion(value: com.example.adopcion_mascotas.domain.model.EstadoPublicacion): String=value.toString()

    @TypeConverter
    fun toEstadoPublicacion(value: String): com.example.adopcion_mascotas.domain.model.EstadoPublicacion = _root_ide_package_.com.example.adopcion_mascotas.domain.model.EstadoPublicacion.valueOf(value)

    @TypeConverter
    fun fromRol(value: com.example.adopcion_mascotas.domain.model.Rol): String=value.toString()

    @TypeConverter
    fun toRol(value: String): com.example.adopcion_mascotas.domain.model.Rol = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Rol.valueOf(value)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String=value.toString()

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime= LocalDateTime.parse(value)



}