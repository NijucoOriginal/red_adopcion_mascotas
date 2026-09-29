package domain

data class Usuario(val id: Long,
                   var nombre_usuario: String,
                   val correo_electronico: String,
                   var contrasenia: String,
                   var rol: Rol,
                   var codigo_multiproposito: String,
                   var primer_nombre: String,
                   var segundo_nombre: String,
                   var primer_apellido: String,
                   var segundo_apellido: String,
                   var direccion: String,
                   var telefono: String,
                   var estado_usuario: EstadoUsuario,
                   val seguidos:MutableList<Usuario>,
                   val notificaciones:MutableList<Notificacion>,
                   val baneos:MutableList<Baneo>,
                   val reputacion: Reputacion
) {
}