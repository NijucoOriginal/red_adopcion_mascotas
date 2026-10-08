package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

@Entity(foreignKeys = [
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuario_duenio"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
        parentColumns = ["id"],
        childColumns = ["moderador"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["usuario_duenio"]),
        Index(value = ["moderador"])
    ]
)
data class Baneo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val titulo: String,
    val razon: String,
    val fecha_emision: LocalDateTime,
    val fecha_inicio: LocalDateTime,
    val fecha_fin: LocalDateTime,
    val usuario_duenio: Long,
    val moderador: Long,
    val estado_baneo: EstadoBaneo
)