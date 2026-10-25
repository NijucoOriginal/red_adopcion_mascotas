package com.example.adopcion_mascotas.features.feed

import androidx.lifecycle.ViewModel
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.Resultado
import com.example.adopcion_mascotas.domain.model.Baneo
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.model.Rol
import com.example.adopcion_mascotas.domain.model.Usuario
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Usuario tal como lo necesita la lista de moderación
data class ItemUsuario(
    val id: Long,
    val nombreUsuario: String,
    val nombreCompleto: String,
    val correo: String,
    val esModerador: Boolean,
    val baneo: Baneo?
) {
    val baneado: Boolean get() = baneo != null
}

data class FeedModUiState(
    val publicaciones: List<Publicacion> = emptyList(),
    val usuarios: List<ItemUsuario> = emptyList(),
    val filtroCategoria: CategoriaPublicacion? = null,
    val filtroUsuarioId: Long? = null,
    val isLoading: Boolean = true,
    val mensaje: String? = null,
    val sesionExpirada: Boolean = false
)

class FeedModViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedModUiState())
    val uiState: StateFlow<FeedModUiState> = _uiState.asStateFlow()

    private var token: String = ""
    private var todas: List<Publicacion> = emptyList()

    fun load(token: String) {
        this.token = token
        refrescar()
    }

    fun refrescar() {
        when (val r = PersistenciaSimulada.publicacionesParaModerador(token)) {
            is Resultado.Exito -> {
                todas = r.datos
            }
            is Resultado.Error -> {
                _uiState.update { it.copy(isLoading = false, mensaje = r.mensaje) }
                return
            }
            Resultado.SesionExpirada -> {
                _uiState.update { it.copy(isLoading = false, sesionExpirada = true) }
                return
            }
        }

        when (val r = PersistenciaSimulada.listarUsuarios(token)) {
            is Resultado.Exito -> {
                val items = r.datos.map { it.aItem() }
                _uiState.update { it.copy(usuarios = items) }
            }
            is Resultado.Error -> {
                _uiState.update { it.copy(mensaje = r.mensaje) }
            }
            Resultado.SesionExpirada -> {
                _uiState.update { it.copy(sesionExpirada = true) }
            }
        }

        _uiState.update { it.copy(isLoading = false) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val s = _uiState.value
        val filtradas = todas.filter { p ->
            (s.filtroCategoria == null || p.categoria == s.filtroCategoria) &&
                    (s.filtroUsuarioId == null || p.usuario_duenio == s.filtroUsuarioId)
        }
        _uiState.update { it.copy(publicaciones = filtradas) }
    }

    fun onFiltroCategoria(categoria: CategoriaPublicacion?) {
        _uiState.update { it.copy(filtroCategoria = categoria) }
        aplicarFiltros()
    }

    fun onFiltroUsuario(usuarioId: Long?) {
        _uiState.update { it.copy(filtroUsuarioId = usuarioId) }
        aplicarFiltros()
    }

    // dias = null significa baneo permanente
    fun banear(usuarioId: Long, titulo: String, razon: String, dias: Long?) {
        val fechaFin = dias?.let { LocalDateTime.now().plusDays(it) }

        when (val r = PersistenciaSimulada.banearUsuario(token, usuarioId, titulo, razon, fechaFin)) {
            is Resultado.Exito -> {
                _uiState.update { it.copy(mensaje = "Usuario baneado") }
                refrescar()
            }
            is Resultado.Error -> _uiState.update { it.copy(mensaje = r.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(sesionExpirada = true) }
        }
    }

    fun levantarBaneo(baneoId: Long) {
        when (val r = PersistenciaSimulada.levantarBaneo(token, baneoId)) {
            is Resultado.Exito -> {
                _uiState.update { it.copy(mensaje = "Baneo levantado") }
                refrescar()
            }
            is Resultado.Error -> _uiState.update { it.copy(mensaje = r.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(sesionExpirada = true) }
        }
    }

    fun cerrarSesion() {
        PersistenciaSimulada.cerrarSesion(token)
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    private fun Usuario.aItem(): ItemUsuario = ItemUsuario(
        id = id,
        nombreUsuario = nombre_usuario,
        nombreCompleto = listOfNotNull(
            primer_nombre,
            segundo_nombre,
            primer_apellido,
            segundo_apellido
        ).joinToString(" "),
        correo = correo_electronico,
        esModerador = rol == Rol.MODERADOR,
        baneo = PersistenciaSimulada.obtenerBaneoActivo(id)
    )
}