package com.example.adopcion_mascotas.core.util

import com.example.adopcion_mascotas.domain.model.*
import java.time.LocalDateTime
import java.util.UUID

// ---------- Tipos de apoyo ----------

data class SesionToken(
    val token: String,
    val correoElectronico: String,
    var ultimaActividad: Long
)

sealed interface ResultadoLogin {
    data class Exito(val token: String, val rol: Rol) : ResultadoLogin
    data class Error(val mensaje: String) : ResultadoLogin
}

sealed interface ResultadoPublicacion {
    data object Exito : ResultadoPublicacion
    data class Error(val mensaje: String) : ResultadoPublicacion
}

sealed interface ResultadoComentario {
    data class Exito(val comentario: Comentario) : ResultadoComentario
    data class Error(val mensaje: String) : ResultadoComentario
    data object SesionExpirada : ResultadoComentario
}

sealed interface ResultadoMeGusta {
    data class Exito(val meGustado: Boolean) : ResultadoMeGusta
    data class Error(val mensaje: String) : ResultadoMeGusta
    data object SesionExpirada : ResultadoMeGusta
}

// Datos que llegan desde el formulario de crear publicación
data class NuevaVacuna(val nombre: String, val dosis: String)

data class NuevaMascota(
    val nombre: String,
    val tipo: TipoAnimal,
    val altura: Double,
    val peso: Double,
    val longitud: Double,
    val vacunas: List<NuevaVacuna>
)

object PersistenciaSimulada {

    private const val TIEMPO_INACTIVIDAD_MS = 20L * 60 * 1000

    // ---------- "Tablas" en memoria ----------

    private val _usuarios = mutableListOf<Usuario>()
    private val _publicaciones = mutableListOf<Publicacion>()
    private val _mascotas = mutableListOf<Mascota>()
    private val _mascotaPublicacion = mutableListOf<Mascota_Publicacion>()
    private val _vacunas = mutableListOf<Vacuna>()
    private val _comentarios = mutableListOf<Comentario>()
    private val _meGustas = mutableListOf<Persona_Que_Le_Gusta>()
    private val _codigosGenerados = mutableListOf<String>()

    private val sesiones = mutableMapOf<String, SesionToken>()

    // Publicación -> correos de los usuarios que marcaron interés
    private val interesados = mutableMapOf<Long, MutableSet<String>>()

    val publicaciones: List<Publicacion> get() = _publicaciones

    init {
        _usuarios.addAll(
            listOf(
                Usuario(
                    id = 101,
                    nombre_usuario = "maria_lopez",
                    correo_electronico = "maria.lopez@correo.com",
                    contrasenia = "Huellas123",
                    rol = Rol.USUARIO,
                    codigo_multiproposito = null,
                    primer_nombre = "María",
                    segundo_nombre = null,
                    primer_apellido = "López",
                    segundo_apellido = null,
                    direccion = "Calle 10 # 15-20, Armenia",
                    telefono = "3001234567",
                    estado_usuario = EstadoUsuario.ACTIVO
                ),
                Usuario(
                    id = 102,
                    nombre_usuario = "carlos_ruiz",
                    correo_electronico = "carlos.ruiz@correo.com",
                    contrasenia = "Huellas456",
                    rol = Rol.USUARIO,
                    codigo_multiproposito = null,
                    primer_nombre = "Carlos",
                    segundo_nombre = null,
                    primer_apellido = "Ruiz",
                    segundo_apellido = null,
                    direccion = "Barrio Granada, Armenia",
                    telefono = "3109876543",
                    estado_usuario = EstadoUsuario.ACTIVO
                ),
                Usuario(
                    id = 103,
                    nombre_usuario = "moderador_huellas",
                    correo_electronico = "moderador@correo.com",
                    contrasenia = "Moderador789",
                    rol = Rol.MODERADOR,
                    codigo_multiproposito = null,
                    primer_nombre = "Andrea",
                    segundo_nombre = null,
                    primer_apellido = "Gómez",
                    segundo_apellido = null,
                    direccion = "Centro, Armenia",
                    telefono = "3205551234",
                    estado_usuario = EstadoUsuario.ACTIVO
                )
            )
        )

        _publicaciones.addAll(
            listOf(
                Publicacion(
                    id = 1,
                    cantidad_interesa = 3,
                    comentarios_activados = true,
                    es_de_moderador = false,
                    fecha_publicacion = LocalDateTime.now(),
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
                    fecha_publicacion = LocalDateTime.now().minusDays(1),
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
                    fecha_publicacion = LocalDateTime.now().minusDays(2),
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

    // ---------- Usuarios y registro ----------

    fun buscarUsuarioExistente(correo: String): Usuario? =
        _usuarios.firstOrNull { it.correo_electronico == correo }

    fun nombreDeUsuario(usuarioId: Long): String =
        _usuarios.firstOrNull { it.id == usuarioId }?.nombre_usuario ?: "Usuario"

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
        if (buscarUsuarioExistente(correo_electronico) != null) return false

        _usuarios.add(
            Usuario(
                id = _usuarios.siguienteId { it.id },
                nombre_usuario = nombre_usuario,
                correo_electronico = correo_electronico,
                contrasenia = contrasenia,
                rol = Rol.USUARIO,
                codigo_multiproposito = codigoMultiproposito,
                primer_nombre = primer_nombre,
                segundo_nombre = segundo_nombre,
                primer_apellido = primer_apellido,
                segundo_apellido = segundo_apellido,
                direccion = direccion,
                telefono = telefono,
                estado_usuario = EstadoUsuario.PENDIENTE
            )
        )
        return true
    }

    fun verificarCodigo(codigo: String, correo: String): Boolean {
        val usuario = buscarUsuarioExistente(correo) ?: return false
        if (codigo != usuario.codigo_multiproposito) return false

        usuario.estado_usuario = EstadoUsuario.ACTIVO
        usuario.codigo_multiproposito = null
        return true
    }

    fun generarCodigoUnico(): String {
        var codigo: String
        do {
            codigo = (10000..99999).random().toString()
        } while (codigo in _codigosGenerados)

        _codigosGenerados.add(codigo)
        return codigo
    }

    // ---------- Sesiones ----------

    fun login(correo: String, contrasenia: String): ResultadoLogin {
        val usuario = buscarUsuarioExistente(correo)
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

    fun correoDeToken(token: String): String? =
        sesiones[token]?.correoElectronico

    // ---------- Publicaciones ----------

    fun obtenerPublicacion(id: Long): Publicacion? =
        _publicaciones.firstOrNull { it.id == id }

    fun crearPublicacion(
        token: String,
        categoria: CategoriaPublicacion,
        descripcion: String,
        nombreUbicacion: String,
        mascota: NuevaMascota,
        comentariosActivados: Boolean = true
    ): ResultadoPublicacion {
        if (!tokenValido(token)) return ResultadoPublicacion.Error("Tu sesión expiró")

        val correo = correoDeToken(token)
            ?: return ResultadoPublicacion.Error("Sesión no válida")
        val usuario = buscarUsuarioExistente(correo)
            ?: return ResultadoPublicacion.Error("Usuario no encontrado")
        if (usuario.estado_usuario != EstadoUsuario.ACTIVO) {
            return ResultadoPublicacion.Error("Tu cuenta no está activa")
        }

        val publicacion = Publicacion(
            id = _publicaciones.siguienteId { it.id },
            cantidad_interesa = 0,
            comentarios_activados = comentariosActivados,
            es_de_moderador = usuario.rol == Rol.MODERADOR,
            fecha_publicacion = LocalDateTime.now(),
            descripcion = descripcion,
            estado_publicacion = EstadoPublicacion.ADMITIDO,
            categoria = categoria,
            usuario_duenio = usuario.id,
            latitud = 0.0,
            longitud = 0.0,
            nombre_ubicacion = nombreUbicacion
        )
        _publicaciones.add(publicacion)

        val mascotaCreada = Mascota(
            id = _mascotas.siguienteId { it.id },
            nombre = mascota.nombre,
            tipo_animal = mascota.tipo,
            altura = mascota.altura,
            peso = mascota.peso,
            longitud = mascota.longitud
        )
        _mascotas.add(mascotaCreada)

        _mascotaPublicacion.add(
            Mascota_Publicacion(
                id_mascota = mascotaCreada.id,
                id_publicacion = publicacion.id
            )
        )

        mascota.vacunas.forEach { vacuna ->
            _vacunas.add(
                Vacuna(
                    id = _vacunas.siguienteId { it.id },
                    nombre = vacuna.nombre,
                    dosis = vacuna.dosis,
                    mascotaAsociada = mascotaCreada.id
                )
            )
        }

        return ResultadoPublicacion.Exito
    }

    // ---------- Intereses ----------

    fun estaInteresado(publicacionId: Long, correo: String): Boolean =
        interesados[publicacionId]?.contains(correo) == true

    // Alterna el interés del usuario y ajusta el contador. Devuelve el nuevo estado.
    fun alternarInteres(publicacionId: Long, correo: String): Boolean {
        val index = _publicaciones.indexOfFirst { it.id == publicacionId }
        if (index == -1) return false

        val conjunto = interesados.getOrPut(publicacionId) { mutableSetOf() }
        val ahoraInteresado = if (conjunto.contains(correo)) {
            conjunto.remove(correo)
            false
        } else {
            conjunto.add(correo)
            true
        }

        val actual = _publicaciones[index]
        val delta = if (ahoraInteresado) 1 else -1
        _publicaciones[index] = actual.copy(
            cantidad_interesa = (actual.cantidad_interesa + delta).coerceAtLeast(0)
        )
        return ahoraInteresado
    }

    // ---------- Mascotas y vacunas ----------

    fun obtenerMascotasDePublicacion(publicacionId: Long): List<Mascota> {
        val idsMascotas = _mascotaPublicacion
            .filter { it.id_publicacion == publicacionId }
            .map { it.id_mascota }
        return _mascotas.filter { it.id in idsMascotas }
    }

    fun obtenerVacunas(mascotaId: Long): List<Vacuna> =
        _vacunas.filter { it.mascotaAsociada == mascotaId }

    // ---------- Comentarios ----------

    fun obtenerComentarios(publicacionId: Long): List<Comentario> =
        _comentarios
            .filter { it.publicacion_perteneciente == publicacionId }
            .sortedBy { it.fecha_publicacion }

    fun agregarComentario(
        token: String,
        publicacionId: Long,
        cuerpo: String
    ): ResultadoComentario {
        if (!tokenValido(token)) return ResultadoComentario.SesionExpirada

        val correo = correoDeToken(token)
            ?: return ResultadoComentario.SesionExpirada
        val usuario = buscarUsuarioExistente(correo)
            ?: return ResultadoComentario.Error("Usuario no encontrado")
        val publicacion = obtenerPublicacion(publicacionId)
            ?: return ResultadoComentario.Error("La publicación no existe")

        if (!publicacion.comentarios_activados) {
            return ResultadoComentario.Error("Los comentarios están desactivados")
        }
        if (cuerpo.isBlank()) {
            return ResultadoComentario.Error("El comentario no puede estar vacío")
        }

        val comentario = Comentario(
            id = _comentarios.siguienteId { it.id },
            fecha_publicacion = LocalDateTime.now(),
            cuerpo = cuerpo.trim(),
            usuario_duenio = usuario.id,
            es_admin = usuario.rol == Rol.MODERADOR,
            respondeA = null,
            cantidad_me_gusta = 0,
            publicacion_perteneciente = publicacionId
        )
        _comentarios.add(comentario)
        return ResultadoComentario.Exito(comentario)
    }

    // ---------- Me gusta en comentarios ----------

    fun estaMeGustado(comentarioId: Long, correo: String): Boolean {
        val usuario = buscarUsuarioExistente(correo) ?: return false
        return _meGustas.any {
            it.id_comentario == comentarioId && it.id_usuario == usuario.id
        }
    }

    fun alternarMeGusta(token: String, comentarioId: Long): ResultadoMeGusta {
        if (!tokenValido(token)) return ResultadoMeGusta.SesionExpirada

        val correo = correoDeToken(token)
            ?: return ResultadoMeGusta.SesionExpirada
        val usuario = buscarUsuarioExistente(correo)
            ?: return ResultadoMeGusta.Error("Usuario no encontrado")
        val comentario = _comentarios.firstOrNull { it.id == comentarioId }
            ?: return ResultadoMeGusta.Error("El comentario no existe")

        val registro = _meGustas.firstOrNull {
            it.id_comentario == comentarioId && it.id_usuario == usuario.id
        }

        return if (registro != null) {
            _meGustas.remove(registro)
            comentario.cantidad_me_gusta = (comentario.cantidad_me_gusta - 1).coerceAtLeast(0)
            ResultadoMeGusta.Exito(meGustado = false)
        } else {
            _meGustas.add(Persona_Que_Le_Gusta(usuario.id, comentarioId))
            comentario.cantidad_me_gusta += 1
            ResultadoMeGusta.Exito(meGustado = true)
        }
    }

    // ---------- Utilidad ----------

    // Siguiente id disponible para una lista de entidades con id Long
    private fun <T> List<T>.siguienteId(idOf: (T) -> Long): Long =
        (maxOfOrNull(idOf) ?: 0L) + 1
}