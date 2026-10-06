package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Mascota::class,
        parentColumns = ["id"],
        childColumns = ["mascotaAsociada"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["mascotaAsociada"])
    ]
    )
data class Vacuna(
                  @PrimaryKey(autoGenerate = true)val id: Long,
                  var nombre: String,
                  var dosis: String,
                  var mascotaAsociada: Long)
