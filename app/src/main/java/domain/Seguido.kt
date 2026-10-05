package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
    ],

    indices = [
        Index(value = ["id_seguido"]),
        Index(value = ["id_duenio"])
    ]
)
data class Seguido(val id_seguido: Long,
                   val id_duenio: Long) {
}