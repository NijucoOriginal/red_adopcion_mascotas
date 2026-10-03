package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [
    ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuario_duenio"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["moderador"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Baneo(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val titulo: String,
    val razon: String,
    val fecha_emision: LocalDateTime,
    val fecha_inicio: LocalDateTime,
    val fecha_fin: LocalDateTime,
    val usuario_duenio: Long,
    val moderador: Usuario,
    val estado_baneo: EstadoBaneo
)