package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
        )],
        indices = [
            Index(value = ["id_mascota"]),
            Index(value = ["id_publicacion"])
    ]
)
data class Mascota_Publicacion(val id_mascota: Long,
                               val id_publicacion: Long) {
}