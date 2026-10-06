package com.example.adopcion_mascotas.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys =[
    ForeignKey(
        entity = _root_ide_package_.com.example.adopcion_mascotas.domain.model.Usuario::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("usuario_perteneciente"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["usuario_perteneciente"])
    ]
    )
data class Reputacion(@PrimaryKey(autoGenerate = true) val id: Long,
                      val usuario_perteneciente: Long,
                      var nivel: Int,
                      var puntos_totales: Int,
                      var titulo: String) {
}