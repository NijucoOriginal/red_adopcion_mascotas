package domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Logro(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var descripcion: String,
    var titulo: String,
    var cantidad_puntos: Int,
    var icono_medalla: String
)
