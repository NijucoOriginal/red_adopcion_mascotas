package domain

import androidx.room.Entity
import androidx.room.ForeignKey

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
    ]
)
data class Persona_Que_Le_Gusta(val id_usuario: Long,
                                val id_comentario: Long ) {
}