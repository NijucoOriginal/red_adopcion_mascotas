package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Publicacion::class,
        parentColumns = ["id"],
        childColumns = ["publicacion_asociada"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["publicacion_asociada"])
    ]
    )
data class Foto(
                @PrimaryKey(autoGenerate = true) val id: Long,
                val url: String,
                val publicacion_asociada: Long) {
}