package com.example.adopcion_mascotas.core.util

import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoUsuario
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.model.Rol
import com.example.adopcion_mascotas.domain.model.Usuario
import java.util.UUID

// ---------- Datos en memoria ----------

val usuarios = mutableListOf<Usuario>()
val codigosGenerados = mutableListOf<String?>()

data class SesionToken(
    val token: String,
    val correoElectronico: String,
    var ultimaActividad: Long
)

sealed interface ResultadoLogin {
    data class Exito(val token: String, val rol: Rol) : ResultadoLogin
    data class Error(val mensaje: String) : ResultadoLogin
}

// ---------- Persistencia simulada: login, sesiones y publicaciones ----------

object PersistenciaSimulada {

    private const val MINUTOS_INACTIVIDAD = 20L
    private const val TIEMPO_INACTIVIDAD_MS = MINUTOS_INACTIVIDAD * 60 * 1000

    // Sesiones activas: token -> sesión
    private val sesiones = mutableMapOf<String, SesionToken>()

    // Publicaciones de prueba para el feed
    private val _publicaciones = mutableListOf<Publicacion>()
    val publicaciones: List<Publicacion> get() = _publicaciones

    init {
        _publicaciones.addAll(
            listOf(
                Publicacion(
                    id = 1,
                    cantidad_interesa = 3,
                    comentarios_activados = true,
                    es_de_moderador = false,
                    fecha_publicacion = java.time.LocalDateTime.now(),
                    descripcion = "Perro mestizo en adopción, muy juguetón.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ADOPCION,
                    usuario_duenio = 101,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                ),
                Publicacion(
                    id = 2,
                    cantidad_interesa = 0,
                    comentarios_activados = true,
                    es_de_moderador = false,
                    fecha_publicacion = java.time.LocalDateTime.now().minusDays(1),
                    descripcion = "Gato reportado como perdido en el barrio Granada.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ENCONTRADOS,
                    usuario_duenio = 102,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                ),
                Publicacion(
                    id = 3,
                    cantidad_interesa = 1,
                    comentarios_activados = false,
                    es_de_moderador = true,
                    fecha_publicacion = java.time.LocalDateTime.now().minusDays(2),
                    descripcion = "Caso cerrado: mascota encontrada y reunida con su dueño.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ENCONTRADOS,
                    usuario_duenio = 103,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                )
            )
        )
    }

    fun addPublicacion(pub: Publicacion) {
        _publicaciones.add(pub)
    }

    fun login(correo: String, contrasenia: String): ResultadoLogin {
        val usuario = usuarios.firstOrNull { it.correo_electronico == correo }
            ?: return ResultadoLogin.Error("Correo o contraseña incorrectos")

        if (usuario.contrasenia != contrasenia) {
            return ResultadoLogin.Error("Correo o contraseña incorrectos")
        }

        if (usuario.estado_usuario != EstadoUsuario.ACTIVO) {
            return ResultadoLogin.Error("Tu cuenta aún no está activa")
        }

        val token = UUID.randomUUID().toString()
        sesiones[token] = SesionToken(
            token = token,
            correoElectronico = correo,
            ultimaActividad = System.currentTimeMillis()
        )
        return ResultadoLogin.Exito(token, usuario.rol)
    }

    // Comprueba el token y renueva el tiempo de inactividad si sigue válido
    fun tokenValido(token: String): Boolean {
        val sesion = sesiones[token] ?: return false
        val ahora = System.currentTimeMillis()

        if (ahora - sesion.ultimaActividad > TIEMPO_INACTIVIDAD_MS) {
            sesiones.remove(token)
            return false
        }

        sesion.ultimaActividad = ahora
        return true
    }

    fun cerrarSesion(token: String) {
        sesiones.remove(token)
    }
}

// ---------- Funciones de registro y activación ----------

fun registrarUsuario(
    nombre_usuario: String,
    correo_electronico: String,
    contrasenia: String,
    primer_nombre: String,
    segundo_nombre: String,
    primer_apellido: String,
    segundo_apellido: String,
    direccion: String,
    telefono: String,
    codigoMultiproposito: String
): Boolean {
    val id: Long = (0..100).random().toLong()
    val existente = buscarUsuarioExistente(correo_electronico)
    if (existente != null) return false

    usuarios.add(
        Usuario(
            id,
            nombre_usuario,
            correo_electronico,
            contrasenia,
            Rol.USUARIO,
            codigoMultiproposito,
            primer_nombre,
            segundo_nombre,
            primer_apellido,
            segundo_apellido,
            direccion,
            telefono,
            EstadoUsuario.PENDIENTE
        )
    )
    return true
}

fun buscarUsuarioExistente(correo_electronico: String): Usuario? {
    return usuarios.firstOrNull { it.correo_electronico == correo_electronico }
}

fun verificarCodigo(codigo: String, correo_electronico: String): Boolean {
    val usuario = buscarUsuarioExistente(correo_electronico) ?: return false
    if (codigo == usuario.codigo_multiproposito) {
        usuario.estado_usuario = EstadoUsuario.ACTIVO
        usuario.codigo_multiproposito = null
        return true
    }
    return false
}

fun llenarCodigosGenerados() {
    usuarios.forEach { usuario ->
        codigosGenerados.add(usuario.codigo_multiproposito)
    }
}

fun generarCodigoUnico(): String {
    var codigo: String
    do {
        codigo = (10000..99999).random().toString()
    } while (codigo in codigosGenerados)

    codigosGenerados.add(codigo)
    return codigo
}