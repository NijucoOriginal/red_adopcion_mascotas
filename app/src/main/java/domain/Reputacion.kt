package domain

data class Reputacion(val id: Long,
                 val usuario_perteneciente: Usuario,
                 var nivel: Int,
                 var puntos_totales: Int,
                 var titulo: String) {
}