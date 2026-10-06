package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
        ForeignKey(
            entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Vacuna::class,
            parentColumns = ["id"],
            childColumns = ["vacuna_asociada"],
            onDelete = ForeignKey.CASCADE
        )],
        indices = [
            Index(value = ["vacuna_asociada"])
    ]
)
data class Foto_Carnet(@PrimaryKey(autoGenerate = true) val id: Long,
                       var url: String,
                       var vacuna_asociada: Long) {
}