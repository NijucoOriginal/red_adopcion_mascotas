package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    primaryKeys = ["id_mascota","id_publicacion"],
    foreignKeys = [
        ForeignKey(
            entity = Mascota::class,
            parentColumns = ["id"],
            childColumns = ["id_mascota"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Publicacion::class,
            parentColumns = ["id"],
            childColumns = ["id_publicacion"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Mascota_Publicacion(val id_mascota: Long,
                               val id_publicacion: Long) {
}