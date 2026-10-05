package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [
    ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuario_duenio"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["usuario_duenio"])
    ]
    )
data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val descripcion: String,
    val enlace: String,
    val fecha: LocalDateTime,
    val usuario_duenio: Long,
    val titulo: String,
    val advertencia_baneo: Boolean
)