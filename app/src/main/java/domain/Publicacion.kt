package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import com.google.android.gms.maps.model.LatLng

@Entity(foreignKeys = [
    ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuario_duenio"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Publicacion(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var cantidad_interesa: Int,
    var comentarios_activados: Boolean,
    val es_de_moderador: Boolean,
    var fecha_publicacion: LocalDateTime,
    var descripcion: String,
    var estado_publicacion: EstadoPublicacion,
    var categoria: CategoriaPublicacion,
    val usuario_duenio: Usuario,
    var latitud: Double,
    var longitud: Double,
    var nombre_ubicacion: String
)
