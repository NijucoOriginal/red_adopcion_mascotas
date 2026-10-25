package com.example.adopcion_mascotas.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.Publicacion
import kotlinx.coroutines.flow.*

data class FeedUiState(
    val publicaciones: List<Publicacion> = emptyList(),
    val filtroSeleccionado: CategoriaPublicacion? = null,
    val isLoading: Boolean = true,
    val sesionExpirada: Boolean = false
)

class FeedViewModel : ViewModel() {

    private val filtro = MutableStateFlow<CategoriaPublicacion?>(null)
    private val sesionExpirada = MutableStateFlow(false)

    // Cada incremento fuerza que la lista se vuelva a calcular
    private val refresco = MutableStateFlow(0)

    val uiState: StateFlow<FeedUiState> =
        combine(filtro, sesionExpirada, refresco) { selected, expirada, _ ->
            FeedUiState(
                publicaciones = PersistenciaSimulada.publicaciones
                    .filter { selected == null || it.categoria == selected },
                filtroSeleccionado = selected,
                isLoading = false,
                sesionExpirada = expirada
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())

    // Se llama al entrar a la pantalla y al volver desde el detalle
    fun validarSesion(token: String) {
        if (!PersistenciaSimulada.tokenValido(token)) {
            sesionExpirada.value = true
        }
    }

    // Recalcula la lista con los datos actuales (por ejemplo, el contador de interesados)
    fun refrescar() {
        refresco.update { it + 1 }
    }

    fun onFilterSelected(token: String, type: CategoriaPublicacion?) {
        if (!PersistenciaSimulada.tokenValido(token)) {
            sesionExpirada.value = true
            return
        }
        filtro.value = if (filtro.value == type) null else type
    }

    fun cerrarSesion(token: String) {
        PersistenciaSimulada.cerrarSesion(token)
    }
}