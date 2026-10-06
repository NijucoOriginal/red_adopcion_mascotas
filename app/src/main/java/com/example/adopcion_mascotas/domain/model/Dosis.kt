package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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
data class Dosis(@PrimaryKey(autoGenerate = true) val id: Long,
                 var fecha: LocalDateTime,
                 var numero_dosis: Byte,
                 var vacuna_asociada: Long) {
}