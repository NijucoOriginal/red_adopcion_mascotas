package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [
        ForeignKey(
            entity = Vacuna::class,
            parentColumns = ["id"],
            childColumns = ["vacuna_asociada"],
            onDelete = ForeignKey.CASCADE
        )],
        indices = [
            Index(value = ["vacuna_asociada"])
    ]
)
data class Foto_Carnet(@PrimaryKey(autoGenerate = true) val id: Long,
                       var url: String,
                       var vacuna_asociada: Long) {
}