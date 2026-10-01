package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(
        entity = Mascota::class,
        parentColumns = ["id"],
        childColumns = ["mascotaAsociada"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Vacuna(
                  @PrimaryKey(autoGenerate = true)val id: Long,
                  var nombre: String,
                  var dosis: String,
                  var mascotaAsociada: Mascota)
