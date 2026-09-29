package domain

import java.time.LocalDateTime

data class Notificacion(
    val id: Long,
    val descripcion: String,
    val enlace: String,
    val fecha: LocalDateTime,
    val usuarioDuenio: Usuario,
    val titulo: String,
    val advertenciaBaneo: Boolean
)