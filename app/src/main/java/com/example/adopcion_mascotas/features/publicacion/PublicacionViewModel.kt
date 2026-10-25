package com.example.adopcion_mascotas.features.publicacion

import androidx.lifecycle.ViewModel
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.ResultadoComentario
import com.example.adopcion_mascotas.core.util.ResultadoMeGusta
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.model.TipoAnimal
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ComentarioItem(
    val id: Long,
    val autor: String,
    val esAdmin: Boolean,
    val cuerpo: String,
    val fecha: LocalDateTime,
    val cantidadMeGusta: Int,
    val meGustado: Boolean
)

data class VacunaItem(
    val id: Long,
    val nombre: String,
    val dosis: String
)

data class MascotaItem(
    val id: Long,
    val nombre: String,
    val tipo: TipoAnimal,
    val vacunas: List<VacunaItem>
)

data class PublicacionUiState(
    val publicacion: Publicacion? = null,
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val isInterested: Boolean = false,
    val mascotas: List<MascotaItem> = emptyList(),
    val comentarios: List<ComentarioItem> = emptyList(),
    val textoComentario: String = "",
    val comentarioError: String? = null,
    val mensaje: String? = null,
    val sesionExpirada: Boolean = false
)

class PublicacionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicacionUiState())
    val uiState: StateFlow<PublicacionUiState> = _uiState.asStateFlow()

    private var loadedId: Long? = null
    private var token: String = ""
    private var correoUsuario: String? = null

    fun load(publicacionId: Long, token: String) {
        if (loadedId == publicacionId) return
        loadedId = publicacionId
        this.token = token
        correoUsuario = PersistenciaSimulada.correoDeToken(token)

        val pub = PersistenciaSimulada.obtenerPublicacion(publicacionId)
        val interesado = correoUsuario?.let {
            PersistenciaSimulada.estaInteresado(publicacionId, it)
        } ?: false

        _uiState.update {
            it.copy(
                publicacion = pub,
                isLoading = false,
                notFound = pub == null,
                isInterested = interesado
            )
        }
        cargarMascotas()
        cargarComentarios()
    }

    private fun cargarMascotas() {
        val publicacionId = loadedId ?: return

        val mascotas = PersistenciaSimulada.obtenerMascotasDePublicacion(publicacionId).map { m ->
            MascotaItem(
                id = m.id,
                nombre = m.nombre,
                tipo = m.tipo_animal,
                vacunas = PersistenciaSimulada.obtenerVacunas(m.id).map { v ->
                    VacunaItem(id = v.id, nombre = v.nombre, dosis = v.dosis)
                }
            )
        }
        _uiState.update { it.copy(mascotas = mascotas) }
    }

    private fun cargarComentarios() {
        val publicacionId = loadedId ?: return
        val correo = correoUsuario

        val items = PersistenciaSimulada.obtenerComentarios(publicacionId).map { c ->
            ComentarioItem(
                id = c.id,
                autor = PersistenciaSimulada.nombreDeUsuario(c.usuario_duenio),
                esAdmin = c.es_admin,
                cuerpo = c.cuerpo,
                fecha = c.fecha_publicacion,
                cantidadMeGusta = c.cantidad_me_gusta,
                meGustado = correo != null &&
                        PersistenciaSimulada.estaMeGustado(c.id, correo)
            )
        }
        _uiState.update { it.copy(comentarios = items) }
    }

    fun onInterestClick() {
        if (!PersistenciaSimulada.tokenValido(token)) {
            _uiState.update { it.copy(sesionExpirada = true) }
            return
        }

        val pub = _uiState.value.publicacion ?: return
        val correo = correoUsuario ?: return

        val ahoraInteresado = PersistenciaSimulada.alternarInteres(pub.id, correo)
        val actualizada = PersistenciaSimulada.obtenerPublicacion(pub.id)

        _uiState.update {
            it.copy(isInterested = ahoraInteresado, publicacion = actualizada)
        }
    }

    fun onContactClick() {
        _uiState.update {
            it.copy(mensaje = "La mensajería estará disponible próximamente")
        }
    }

    fun onTextoComentarioChange(value: String) {
        _uiState.update { it.copy(textoComentario = value, comentarioError = null) }
    }

    fun onEnviarComentario() {
        val s = _uiState.value
        val pub = s.publicacion ?: return

        when (val resultado = PersistenciaSimulada.agregarComentario(
            token = token,
            publicacionId = pub.id,
            cuerpo = s.textoComentario
        )) {
            is ResultadoComentario.Exito -> {
                _uiState.update { it.copy(textoComentario = "", comentarioError = null) }
                cargarComentarios()
            }
            is ResultadoComentario.Error -> {
                _uiState.update { it.copy(comentarioError = resultado.mensaje) }
            }
            ResultadoComentario.SesionExpirada -> {
                _uiState.update { it.copy(sesionExpirada = true) }
            }
        }
    }

    fun onMeGustaClick(comentarioId: Long) {
        when (val resultado = PersistenciaSimulada.alternarMeGusta(token, comentarioId)) {
            is ResultadoMeGusta.Exito -> cargarComentarios()
            is ResultadoMeGusta.Error -> {
                _uiState.update { it.copy(mensaje = resultado.mensaje) }
            }
            ResultadoMeGusta.SesionExpirada -> {
                _uiState.update { it.copy(sesionExpirada = true) }
            }
        }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }
}