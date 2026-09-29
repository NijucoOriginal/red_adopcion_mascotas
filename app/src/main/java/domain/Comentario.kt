package domain

import java.time.LocalDateTime

data class Comentario(val id: Long,
                      var fechaPublicacion: LocalDateTime,
                      var cuerpo: String,
                      val usuarioDuenio: Usuario,
                      val esAdmin: Boolean,
                      val respondeA: Comentario? = null,
                      var personasQueLesGusta: MutableList<Usuario>,
                      var cantidadMeGusta: Int,
                      val publicacionPerteneciente: Publicacion) {
}