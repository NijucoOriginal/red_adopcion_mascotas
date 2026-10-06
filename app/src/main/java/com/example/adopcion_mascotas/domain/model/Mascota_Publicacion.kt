package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    primaryKeys = ["id_mascota","id_publicacion"],
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Mascota::class,
            parentColumns = ["id"],
            childColumns = ["id_mascota"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Publicacion::class,
            parentColumns = ["id"],
            childColumns = ["id_publicacion"],
            onDelete = ForeignKey.CASCADE
        )],
        indices = [
            Index(value = ["id_mascota"]),
            Index(value = ["id_publicacion"])
    ]
)
data class Mascota_Publicacion(val id_mascota: Long,
                               val id_publicacion: Long) {
}