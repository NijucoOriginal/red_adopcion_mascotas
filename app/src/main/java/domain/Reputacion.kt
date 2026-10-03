package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys =[
    ForeignKey(
        entity = Usuario::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("usuario_perteneciente"),
        onDelete = ForeignKey.CASCADE
    )
])
data class Reputacion(@PrimaryKey(autoGenerate = true) val id: Long,
                      val usuario_perteneciente: Long,
                      var nivel: Int,
                      var puntos_totales: Int,
                      var titulo: String) {
}