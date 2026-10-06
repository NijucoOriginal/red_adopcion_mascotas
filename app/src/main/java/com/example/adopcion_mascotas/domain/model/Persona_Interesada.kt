package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["usuario_interesado","publicacion_id"],
    foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_interesado"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Publicacion::class,
            parentColumns = ["id"],
            childColumns = ["publicacion_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuario_interesado"]),
        Index(value = ["publicacion_id"])
    ]
)
data class Persona_Interesada(val usuario_interesado: Long,
                              val publicacion_id: Long) {
}