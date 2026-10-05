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
    ),
    ForeignKey(
        entity = Comentario::class,
        parentColumns = ["id"],
        childColumns = ["respondeA"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Publicacion::class,
        parentColumns = ["id"],
        childColumns = ["publicacion_perteneciente"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["usuario_duenio"]),
        Index(value = ["respondeA"]),
        Index(value = ["publicacion_perteneciente"]),
    ]
    )
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var fecha_publicacion: LocalDateTime,
    var cuerpo: String,
    val usuario_duenio: Long,
    val es_admin: Boolean,
    val respondeA: Long?,
    var cantidad_me_gusta: Int,
    val publicacion_perteneciente: Long) {
}