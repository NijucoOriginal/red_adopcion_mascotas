package domain

import java.time.LocalDateTime
import com.google.android.gms.maps.model.LatLng


data class Publicacion(
    val id: Long,
    val personasInteresadas: MutableList<Usuario>,
    var ubicacionLatLng: LatLng,
    var cantidadInteresa: Int,
    var comentariosActivados: Boolean,
    val esDeModerador: Boolean,
    var fechaPublicacion: LocalDateTime,
    var fotos: ArrayList<String>,
    var descripcion: String,
    var estadoPublicacion: EstadoPublicacion,
    var categoria: CategoriaPublicacion,
    val mascotas: MutableList<Mascota>,
    val usuarioDuenio: Usuario,
    val comentarios: MutableList<Comentario>
)
