package domain

import java.time.LocalDateTime

data class Baneo(
    val id: Long,
    val titulo: String,
    val razon: String,
    val fechaEmision: LocalDateTime,
    val fechaInicio: LocalDateTime,
    val fechaFin: LocalDateTime,
    val usuarioDuenio: Usuario,
    val moderador: Usuario,
    val estadoBaneo: EstadoBaneo
)