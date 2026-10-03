package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = Publicacion::class,
        parentColumns = ["id"],
        childColumns = ["publicacion_asociada"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Foto(
                @PrimaryKey(autoGenerate = true) val id: Long,
                val url: String,
                val publicacion_asociada: Long) {
}