package com.example.adopcion_mascotas.features.feed

import androidx.lifecycle.ViewModel
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.Resultado
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ItemHistorial(
    val id: Long,
    val descripcion: String,
    val categoria: CategoriaPublicacion,
    val estado: EstadoPublicacion,
    val fecha: LocalDateTime,
    val oculta: Boolean,
    val comentariosActivados: Boolean,
    val interesados: Int
)

data class HistorialUiState(
    val publicaciones: List<ItemHistorial> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val sesionExpirada: Boolean = false
)

class HistorialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    private var token: String = ""

    // Guarda el token y carga la lista
    fun load(token: String) {
        this.token = token
        cargar()
    }

    fun cargar() {
        when (val resultado = PersistenciaSimulada.obtenerMisPublicaciones(token)) {
            is Resultado.Exito -> {
                val items = resultado.datos.map { p ->
                    ItemHistorial(
                        id = p.id,
                        descripcion = p.descripcion,
                        categoria = p.categoria,
                        estado = p.estado_publicacion,
                        fecha = p.fecha_publicacion,
                        oculta = PersistenciaSimulada.estaOculta(p.id),
                        comentariosActivados = p.comentarios_activados,
                        interesados = p.cantidad_interesa
                    )
                }
                _uiState.update { it.copy(publicaciones = items, isLoading = false, error = null) }
            }
            is Resultado.Error -> {
                _uiState.update { it.copy(isLoading = false, error = resultado.mensaje) }
            }
            Resultado.SesionExpirada -> {
                _uiState.update { it.copy(isLoading = false, sesionExpirada = true) }
            }
        }
    }
}