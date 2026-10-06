package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id_seguido","id_duenio"],
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
            parentColumns = ["id"],
            childColumns = ["id_seguido"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
            parentColumns = ["id"],
            childColumns = ["id_duenio"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["id_seguido"]),
        Index(value = ["id_duenio"])
    ]
)
data class Seguido(val id_seguido: Long,
                   val id_duenio: Long) {
}