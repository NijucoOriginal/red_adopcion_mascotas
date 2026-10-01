package base_datos

import androidx.room.TypeConverter
import domain.CategoriaPublicacion
import domain.EstadoBaneo
import domain.EstadoPublicacion
import domain.EstadoUsuario
import domain.Rol
import domain.TipoAnimal

class Conversor {

    @TypeConverter
    fun fromEstadoUsuario(value: EstadoUsuario): String=value.toString()

    @TypeConverter
    fun toEstadoUsuario(value: String): EstadoUsuario= EstadoUsuario.valueOf(value)

    @TypeConverter
    fun fromEstadoBaneo(value: EstadoBaneo): String=value.toString()

    @TypeConverter
    fun toEstadoBaneo(value: String): EstadoBaneo= EstadoBaneo.valueOf(value)

    @TypeConverter
    fun fromTipoAnimal(value: TipoAnimal): String=value.toString()

    @TypeConverter
    fun toTipoAnimal(value: String): TipoAnimal= TipoAnimal.valueOf(value)

    @TypeConverter
    fun fromCategoriaPublicacion(value: CategoriaPublicacion): String=value.toString()

    @TypeConverter
    fun toCategoriaPublicacion(value: String): CategoriaPublicacion= CategoriaPublicacion.valueOf(value)

    @TypeConverter
    fun fromEstadoPublicacion(value: EstadoPublicacion): String=value.toString()

    @TypeConverter
    fun toEstadoPublicacion(value: String): EstadoPublicacion= EstadoPublicacion.valueOf(value)

    @TypeConverter
    fun fromRol(value: Rol): String=value.toString()

    @TypeConverter
    fun toRol(value: String): Rol= Rol.valueOf(value)



}