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

// Resultado genérico para operaciones que devuelven datos o pueden fallar por sesión o permisos
sealed interface Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>
    data class Error(val mensaje: String) : Resultado<Nothing>
    data object SesionExpirada : Resultado<Nothing>
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

// Datos que llegan desde el formulario de edición de publicación
data class EdicionPublicacion(
    val comentariosActivados: Boolean,
    val descripcion: String,
    val estado: EstadoPublicacion,
    val categoria: CategoriaPublicacion,
    val nombreUbicacion: String,
    val latitud: Double,
    val longitud: Double
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
    private val _baneos = mutableListOf<Baneo>()
    private val _codigosGenerados = mutableListOf<String>()

    private val sesiones = mutableMapOf<String, SesionToken>()

    // Publicación -> correos de los usuarios que marcaron interés
    private val interesados = mutableMapOf<Long, MutableSet<String>>()

    // Ids de publicaciones ocultas por su dueño
    private val _ocultas = mutableSetOf<Long>()

    // Estados que puede ver cualquier usuario en el feed y en el detalle
    private val ESTADOS_VISIBLES_EN_FEED = setOf(
        EstadoPublicacion.ADMITIDO,
        EstadoPublicacion.RESUELTO
    )

    // Lo que ve el feed: visibles, no ocultas por el dueño, con estado permitido
    // y de dueños que no estén baneados
    val publicaciones: List<Publicacion>
        get() = _publicaciones.filter {
            it.id !in _ocultas &&
                    it.estado_publicacion in ESTADOS_VISIBLES_EN_FEED &&
                    !estaBaneado(it.usuario_duenio)
        }

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
        // Un correo ya usado (incluso por un usuario baneado) no se puede registrar de nuevo
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

        // Baneo activo: no puede iniciar sesión
        baneoActivo(usuario.id)?.let { baneo ->
            return ResultadoLogin.Error("Tu cuenta está baneada. Motivo: ${baneo.razon}")
        }

        // Si el baneo ya venció, la cuenta vuelve a estar activa
        if (usuario.estado_usuario == EstadoUsuario.BANEADO) {
            usuario.estado_usuario = EstadoUsuario.ACTIVO
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

    private fun cerrarSesionesDe(correo: String) {
        sesiones.entries.removeAll { it.value.correoElectronico == correo }
    }

    private fun usuarioDeSesion(token: String): Resultado<Usuario> {
        if (!tokenValido(token)) return Resultado.SesionExpirada
        val correo = correoDeToken(token) ?: return Resultado.SesionExpirada
        val usuario = buscarUsuarioExistente(correo)
            ?: return Resultado.Error("Usuario no encontrado")
        return Resultado.Exito(usuario)
    }

    private fun moderadorDeSesion(token: String): Resultado<Usuario> =
        when (val r = usuarioDeSesion(token)) {
            is Resultado.Exito -> {
                if (r.datos.rol == Rol.MODERADOR) {
                    r
                } else {
                    Resultado.Error("Solo los moderadores pueden hacer esto")
                }
            }
            is Resultado.Error -> r
            Resultado.SesionExpirada -> Resultado.SesionExpirada
        }

    // ---------- Publicaciones ----------

    fun obtenerPublicacion(id: Long): Publicacion? =
        _publicaciones.firstOrNull { it.id == id }

    fun estaOculta(publicacionId: Long): Boolean =
        publicacionId in _ocultas

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
            // Las de moderadores nacen admitidas; las de usuarios, pendientes de revisión
            estado_publicacion = if (usuario.rol == Rol.MODERADOR) {
                EstadoPublicacion.ADMITIDO
            } else {
                EstadoPublicacion.PENDIENTE
            },
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

    // Acceso al detalle: dueño y moderadores ven cualquier publicación;
    // el resto solo ve las que aparecen en el feed
    fun obtenerPublicacionVisible(token: String, id: Long): Resultado<Publicacion> {
        val usuario = when (val r = usuarioDeSesion(token)) {
            is Resultado.Exito -> r.datos
            is Resultado.Error -> return r
            Resultado.SesionExpirada -> return Resultado.SesionExpirada
        }

        val pub = _publicaciones.firstOrNull { it.id == id }
            ?: return Resultado.Error("La publicación no existe")

        val esDueno = pub.usuario_duenio == usuario.id
        val esModerador = usuario.rol == Rol.MODERADOR
        if (esDueno || esModerador) return Resultado.Exito(pub)

        val visible = pub.id !in _ocultas &&
                pub.estado_publicacion in ESTADOS_VISIBLES_EN_FEED &&
                !estaBaneado(pub.usuario_duenio)

        return if (visible) {
            Resultado.Exito(pub)
        } else {
            Resultado.Error("La publicación no está disponible")
        }
    }

    // ---------- Historial y edición del dueño ----------

    // Todas las publicaciones del usuario de la sesión, incluidas ocultas, pendientes y rechazadas
    fun obtenerMisPublicaciones(token: String): Resultado<List<Publicacion>> =
        when (val r = usuarioDeSesion(token)) {
            is Resultado.Exito -> Resultado.Exito(
                _publicaciones
                    .filter { it.usuario_duenio == r.datos.id }
                    .sortedByDescending { it.fecha_publicacion }
            )
            is Resultado.Error -> r
            Resultado.SesionExpirada -> Resultado.SesionExpirada
        }

    fun obtenerMiPublicacion(token: String, id: Long): Resultado<Publicacion> =
        conPublicacionDeDueno<Publicacion>(token, id) { Resultado.Exito(it) }

    fun actualizarMiPublicacion(
        token: String,
        id: Long,
        edicion: EdicionPublicacion
    ): Resultado<Publicacion> =
        conPublicacionDeDueno<Publicacion>(token, id) { actual ->
            // El estado solo puede pasar de ADMITIDO a RESUELTO
            val estadoFinal = when {
                edicion.estado == actual.estado_publicacion -> actual.estado_publicacion
                actual.estado_publicacion == EstadoPublicacion.ADMITIDO &&
                        edicion.estado == EstadoPublicacion.RESUELTO -> EstadoPublicacion.RESUELTO
                else -> return@conPublicacionDeDueno Resultado.Error(
                    "Solo puedes cambiar el estado de Admitida a Resuelta"
                )
            }

            if (edicion.descripcion.trim().length < 20) {
                return@conPublicacionDeDueno Resultado.Error(
                    "La descripción debe tener al menos 20 caracteres"
                )
            }
            if (edicion.nombreUbicacion.isBlank()) {
                return@conPublicacionDeDueno Resultado.Error("La ubicación no puede estar vacía")
            }

            val actualizada = actual.copy(
                comentarios_activados = edicion.comentariosActivados,
                descripcion = edicion.descripcion.trim(),
                estado_publicacion = estadoFinal,
                categoria = edicion.categoria,
                nombre_ubicacion = edicion.nombreUbicacion.trim(),
                latitud = edicion.latitud,
                longitud = edicion.longitud
            )

            _publicaciones[_publicaciones.indexOfFirst { it.id == actual.id }] = actualizada
            Resultado.Exito(actualizada)
        }

    // Devuelve true si la publicación queda oculta, false si vuelve a mostrarse
    fun alternarOcultarMiPublicacion(token: String, id: Long): Resultado<Boolean> =
        conPublicacionDeDueno<Boolean>(token, id) { pub ->
            val ahoraOculta = if (pub.id in _ocultas) {
                _ocultas.remove(pub.id)
                false
            } else {
                _ocultas.add(pub.id)
                true
            }
            Resultado.Exito(ahoraOculta)
        }

    fun eliminarMiPublicacion(token: String, id: Long): Resultado<Unit> =
        conPublicacionDeDueno<Unit>(token, id) { pub ->
            eliminarPublicacionCompleta(pub.id)
            Resultado.Exito(Unit)
        }

    // El dueño vuelve a enviar a revisión una publicación rechazada
    fun reenviarMiPublicacion(token: String, id: Long): Resultado<Publicacion> =
        conPublicacionDeDueno<Publicacion>(token, id) { actual ->
            if (actual.estado_publicacion != EstadoPublicacion.RECHAZADO) {
                return@conPublicacionDeDueno Resultado.Error(
                    "Solo puedes reenviar publicaciones rechazadas"
                )
            }

            val actualizada = actual.copy(estado_publicacion = EstadoPublicacion.PENDIENTE)
            _publicaciones[_publicaciones.indexOfFirst { it.id == actual.id }] = actualizada
            Resultado.Exito(actualizada)
        }

    // Borra la publicación y todo lo que depende de ella
    private fun eliminarPublicacionCompleta(publicacionId: Long) {
        // Mascotas ligadas a esta publicación
        val mascotaIds = _mascotaPublicacion
            .filter { it.id_publicacion == publicacionId }
            .map { it.id_mascota }
        _mascotaPublicacion.removeAll { it.id_publicacion == publicacionId }

        // Solo se borran las mascotas que no estén ligadas a otra publicación
        val mascotasHuerfanas = mascotaIds.filter { mascotaId ->
            _mascotaPublicacion.none { it.id_mascota == mascotaId }
        }
        _vacunas.removeAll { it.mascotaAsociada in mascotasHuerfanas }
        _mascotas.removeAll { it.id in mascotasHuerfanas }

        // Comentarios de la publicación y sus "me gusta"
        val comentarioIds = _comentarios
            .filter { it.publicacion_perteneciente == publicacionId }
            .map { it.id }
        _meGustas.removeAll { it.id_comentario in comentarioIds }
        _comentarios.removeAll { it.id in comentarioIds }

        interesados.remove(publicacionId)
        _ocultas.remove(publicacionId)
        _publicaciones.removeAll { it.id == publicacionId }
    }

    // Comprueba sesión y que el usuario sea dueño antes de ejecutar la acción
    private fun <T> conPublicacionDeDueno(
        token: String,
        id: Long,
        accion: (Publicacion) -> Resultado<T>
    ): Resultado<T> {
        val usuario = when (val r = usuarioDeSesion(token)) {
            is Resultado.Exito -> r.datos
            is Resultado.Error -> return r
            Resultado.SesionExpirada -> return Resultado.SesionExpirada
        }

        val publicacion = _publicaciones.firstOrNull { it.id == id }
            ?: return Resultado.Error("La publicación no existe")
        if (publicacion.usuario_duenio != usuario.id) {
            return Resultado.Error("No puedes modificar esta publicación")
        }

        return accion(publicacion)
    }

    // ---------- Moderación ----------

    // El moderador solo puede admitir o rechazar publicaciones pendientes
    fun moderarPublicacion(
        token: String,
        id: Long,
        nuevoEstado: EstadoPublicacion
    ): Resultado<Publicacion> {
        when (val r = moderadorDeSesion(token)) {
            is Resultado.Exito -> Unit
            is Resultado.Error -> return r
            Resultado.SesionExpirada -> return Resultado.SesionExpirada
        }

        if (nuevoEstado != EstadoPublicacion.ADMITIDO && nuevoEstado != EstadoPublicacion.RECHAZADO) {
            return Resultado.Error("Solo puedes admitir o rechazar una publicación")
        }

        val index = _publicaciones.indexOfFirst { it.id == id }
        if (index == -1) return Resultado.Error("La publicación no existe")

        val actual = _publicaciones[index]
        if (actual.estado_publicacion != EstadoPublicacion.PENDIENTE) {
            return Resultado.Error("Solo se pueden moderar publicaciones pendientes")
        }

        val actualizada = actual.copy(estado_publicacion = nuevoEstado)
        _publicaciones[index] = actualizada

        // TODO: notificación de rechazo al dueño (pendiente de implementar)
        return Resultado.Exito(actualizada)
    }

    // ---------- Baneos ----------

    fun estaBaneado(usuarioId: Long): Boolean = baneoActivo(usuarioId) != null

    private fun baneoActivo(usuarioId: Long): Baneo? {
        val ahora = LocalDateTime.now()
        return _baneos.firstOrNull {
            it.usuario_duenio == usuarioId &&
                    it.estado_baneo == EstadoBaneo.ACTIVO &&
                    it.fecha_fin.isAfter(ahora)
        }
    }

    // fechaFin = null significa baneo permanente
    fun banearUsuario(
        token: String,
        usuarioId: Long,
        titulo: String,
        razon: String,
        fechaFin: LocalDateTime?
    ): Resultado<Baneo> {
        val moderador = when (val r = moderadorDeSesion(token)) {
            is Resultado.Exito -> r.datos
            is Resultado.Error -> return r
            Resultado.SesionExpirada -> return Resultado.SesionExpirada
        }

        val objetivo = _usuarios.firstOrNull { it.id == usuarioId }
            ?: return Resultado.Error("El usuario no existe")

        if (objetivo.id == moderador.id) {
            return Resultado.Error("No puedes banearte a ti mismo")
        }
        if (objetivo.rol == Rol.MODERADOR) {
            return Resultado.Error("No puedes banear a otro moderador")
        }
        if (titulo.isBlank()) return Resultado.Error("El título del baneo es obligatorio")
        if (razon.isBlank()) return Resultado.Error("La razón del baneo es obligatoria")
        if (estaBaneado(objetivo.id)) return Resultado.Error("Este usuario ya está baneado")

        val ahora = LocalDateTime.now()
        if (fechaFin != null && !fechaFin.isAfter(ahora)) {
            return Resultado.Error("La fecha de fin debe ser futura")
        }

        val baneo = Baneo(
            id = _baneos.siguienteId { it.id },
            titulo = titulo.trim(),
            razon = razon.trim(),
            fecha_emision = ahora,
            fecha_inicio = ahora,
            fecha_fin = fechaFin ?: LocalDateTime.of(9999, 12, 31, 23, 59),
            usuario_duenio = objetivo.id,
            moderador = moderador.id,
            estado_baneo = EstadoBaneo.ACTIVO
        )
        _baneos.add(baneo)

        objetivo.estado_usuario = EstadoUsuario.BANEADO
        cerrarSesionesDe(objetivo.correo_electronico)

        // TODO: notificación de baneo (pendiente de implementar)
        return Resultado.Exito(baneo)
    }

    fun levantarBaneo(token: String, baneoId: Long): Resultado<Unit> {
        when (val r = moderadorDeSesion(token)) {
            is Resultado.Exito -> Unit
            is Resultado.Error -> return r
            Resultado.SesionExpirada -> return Resultado.SesionExpirada
        }

        val index = _baneos.indexOfFirst { it.id == baneoId }
        if (index == -1) return Resultado.Error("El baneo no existe")

        val baneo = _baneos[index]
        if (baneo.estado_baneo != EstadoBaneo.ACTIVO) {
            return Resultado.Error("Este baneo ya no está activo")
        }

        _baneos[index] = baneo.copy(estado_baneo = EstadoBaneo.LEVANTADO)
        _usuarios.firstOrNull { it.id == baneo.usuario_duenio }?.let {
            if (it.estado_usuario == EstadoUsuario.BANEADO) {
                it.estado_usuario = EstadoUsuario.ACTIVO
            }
        }
        return Resultado.Exito(Unit)
    }

    // ---------- Intereses ----------

    fun estaInteresado(publicacionId: Long, correo: String): Boolean =
        interesados[publicacionId]?.contains(correo) == true

    // Alterna el interés del usuario y ajusta el contador. Devuelve el nuevo estado.
    fun alternarInteres(publicacionId: Long, correo: String): Boolean {
        val index = _publicaciones.indexOfFirst { it.id == publicacionId }
        if (index == -1) return false

        // Sin interés en publicaciones que no están visibles para el público
        if (_publicaciones[index].estado_publicacion !in ESTADOS_VISIBLES_EN_FEED) return false

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

        // Nadie comenta en publicaciones pendientes, rechazadas u ocultas
        if (publicacion.estado_publicacion !in ESTADOS_VISIBLES_EN_FEED) {
            return ResultadoComentario.Error("No se puede comentar en esta publicación")
        }
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