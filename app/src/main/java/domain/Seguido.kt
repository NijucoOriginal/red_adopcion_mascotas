package domain

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["id_seguido","id_duenio"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["id_seguido"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["id_duenio"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Seguido(val id_seguido: Long,
                   val id_duenio: Long) {
}