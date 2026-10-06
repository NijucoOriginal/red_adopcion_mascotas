package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuario_duenio"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["usuario_duenio"])
    ]
    )
data class Publicacion(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var cantidad_interesa: Int,
    var comentarios_activados: Boolean,
    val es_de_moderador: Boolean,
    var fecha_publicacion: LocalDateTime,
    var descripcion: String,
    var estado_publicacion: com.example.adopcion_mascotas.domain.model.EstadoPublicacion,
    var categoria: com.example.adopcion_mascotas.domain.model.CategoriaPublicacion,
    val usuario_duenio: Long,
    var latitud: Double,
    var longitud: Double,
    var nombre_ubicacion: String
)
