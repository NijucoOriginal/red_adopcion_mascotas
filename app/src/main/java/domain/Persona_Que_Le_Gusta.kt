package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id_usuario","id_comentario"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["id_usuario"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Comentario::class,
            parentColumns = ["id"],
            childColumns = ["id_comentario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_usuario"]),
        Index(value = ["id_comentario"])
    ]
)
data class Persona_Que_Le_Gusta(val id_usuario: Long,
                                val id_comentario: Long ) {
}