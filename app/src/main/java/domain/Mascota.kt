package domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mascota(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var nombre: String,
    var tipo_animal: TipoAnimal,
    var altura: Double,
    var peso: Double,
    var longitud: Double,
)