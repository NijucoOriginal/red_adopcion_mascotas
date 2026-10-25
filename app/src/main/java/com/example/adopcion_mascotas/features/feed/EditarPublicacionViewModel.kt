package com.example.adopcion_mascotas.features.historial

import androidx.lifecycle.ViewModel
import com.example.adopcion_mascotas.core.util.EdicionPublicacion
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.Resultado
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditarPublicacionUiState(
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val estadoOriginal: EstadoPublicacion? = null,
    val comentariosActivados: Boolean = true,
    val descripcion: String = "",
    val marcarResuelta: Boolean = false,
    val categoria: CategoriaPublicacion = CategoriaPublicacion.ADOPCION,
    val nombreUbicacion: String = "",
    val latitud: String = "",
    val longitud: String = "",
    val oculta: Boolean = false,
    val descripcionError: String? = null,
    val ubicacionError: String? = null,
    val coordenadasError: String? = null,
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val guardadoExitoso: Boolean = false,
    val eliminada: Boolean = false,
    val sesionExpirada: Boolean = false
)

class EditarPublicacionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPublicacionUiState())
    val uiState: StateFlow<EditarPublicacionUiState> = _uiState.asStateFlow()

    private var token: String = ""
    private var publicacionId: Long = -1
    private var cargado = false

    fun load(publicacionId: Long, token: String) {
        if (cargado && this.publicacionId == publicacionId) return
        this.publicacionId = publicacionId
        this.token = token
        cargado = true

        when (val resultado = PersistenciaSimulada.obtenerMiPublicacion(token, publicacionId)) {
            is Resultado.Exito -> {
                val p = resultado.datos
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        estadoOriginal = p.estado_publicacion,
                        comentariosActivados = p.comentarios_activados,
                        descripcion = p.descripcion,
                        categoria = p.categoria,
                        nombreUbicacion = p.nombre_ubicacion,
                        latitud = p.latitud.toString(),
                        longitud = p.longitud.toString(),
                        oculta = PersistenciaSimulada.estaOculta(p.id)
                    )
                }
            }
            is Resultado.Error -> {
                _uiState.update { it.copy(isLoading = false, notFound = true, mensaje = resultado.mensaje) }
            }
            Resultado.SesionExpirada -> {
                _uiState.update { it.copy(isLoading = false, sesionExpirada = true) }
            }
        }
    }

    fun onComentariosChange(value: Boolean) =
        _uiState.update { it.copy(comentariosActivados = value) }

    fun onDescripcionChange(value: String) =
        _uiState.update { it.copy(descripcion = value, descripcionError = null) }

    fun onMarcarResueltaChange(value: Boolean) =
        _uiState.update { it.copy(marcarResuelta = value) }

    fun onCategoriaChange(value: CategoriaPublicacion) =
        _uiState.update { it.copy(categoria = value) }

    fun onNombreUbicacionChange(value: String) =
        _uiState.update { it.copy(nombreUbicacion = value, ubicacionError = null) }

    fun onLatitudChange(value: String) =
        _uiState.update { it.copy(latitud = value, coordenadasError = null) }

    fun onLongitudChange(value: String) =
        _uiState.update { it.copy(longitud = value, coordenadasError = null) }

    fun onGuardarClick() {
        val s = _uiState.value
        val original = s.estadoOriginal ?: return

        val lat = medida(s.latitud)
        val lon = medida(s.longitud)

        val descError = if (s.descripcion.trim().length < 20) {
            "La descripción debe tener al menos 20 caracteres"
        } else null
        val ubiError = if (s.nombreUbicacion.isBlank()) "Ingresa la ubicación" else null
        val coordError = if (lat == null || lon == null) {
            "Usa solo números en latitud y longitud"
        } else null

        _uiState.update {
            it.copy(
                descripcionError = descError,
                ubicacionError = ubiError,
                coordenadasError = coordError
            )
        }
        if (descError != null || ubiError != null || coordError != null) return

        // Solo cambia si el usuario marcó la opción y el estado original es ADMITIDO
        val estadoNuevo = if (s.marcarResuelta && original == EstadoPublicacion.ADMITIDO) {
            EstadoPublicacion.RESUELTO
        } else {
            original
        }

        _uiState.update { it.copy(guardando = true, mensaje = null) }

        val resultado = PersistenciaSimulada.actualizarMiPublicacion(
            token = token,
            id = publicacionId,
            edicion = EdicionPublicacion(
                comentariosActivados = s.comentariosActivados,
                descripcion = s.descripcion,
                estado = estadoNuevo,
                categoria = s.categoria,
                nombreUbicacion = s.nombreUbicacion,
                latitud = lat ?: 0.0,
                longitud = lon ?: 0.0
            )
        )

        when (resultado) {
            is Resultado.Exito -> _uiState.update { it.copy(guardando = false, guardadoExitoso = true) }
            is Resultado.Error -> _uiState.update { it.copy(guardando = false, mensaje = resultado.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(guardando = false, sesionExpirada = true) }
        }
    }

    // El dueño reenvía una publicación rechazada a revisión
    fun onReenviarClick() {
        when (val r = PersistenciaSimulada.reenviarMiPublicacion(token, publicacionId)) {
            is Resultado.Exito -> _uiState.update {
                it.copy(
                    estadoOriginal = EstadoPublicacion.PENDIENTE,
                    mensaje = "Publicación enviada a revisión"
                )
            }
            is Resultado.Error -> _uiState.update { it.copy(mensaje = r.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(sesionExpirada = true) }
        }
    }

    fun onAlternarOcultarClick() {
        when (val resultado = PersistenciaSimulada.alternarOcultarMiPublicacion(token, publicacionId)) {
            is Resultado.Exito -> _uiState.update { it.copy(oculta = resultado.datos) }
            is Resultado.Error -> _uiState.update { it.copy(mensaje = resultado.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(sesionExpirada = true) }
        }
    }

    fun onEliminarConfirmado() {
        when (val resultado = PersistenciaSimulada.eliminarMiPublicacion(token, publicacionId)) {
            is Resultado.Exito -> _uiState.update { it.copy(eliminada = true) }
            is Resultado.Error -> _uiState.update { it.copy(mensaje = resultado.mensaje) }
            Resultado.SesionExpirada -> _uiState.update { it.copy(sesionExpirada = true) }
        }
    }

    // Campo vacío = 0.0; texto no numérico = null (error)
    private fun medida(texto: String): Double? =
        if (texto.isBlank()) 0.0 else texto.replace(',', '.').toDoubleOrNull()
}