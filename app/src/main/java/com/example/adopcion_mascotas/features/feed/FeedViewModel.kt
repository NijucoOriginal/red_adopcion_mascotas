package com.example.adopcion_mascotas.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.repository.FakePublicacionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted

data class FeedUiState(
    val publicaciones: List<Publicacion> = emptyList(),
    val filtroSeleccionado: CategoriaPublicacion? = null,
    val isLoading: Boolean = true
)

class FeedViewModel : ViewModel() {
    private val filtro = MutableStateFlow<CategoriaPublicacion?>(null)

    val uiState: StateFlow<FeedUiState> =
        filtro.map { selected ->
            FeedUiState(
                publicaciones = FakePublicacionRepository.publicaciones.filter { selected == null || it.categoria == selected },
                filtroSeleccionado = selected,
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())

    fun onFilterSelected(type: CategoriaPublicacion?) {
        filtro.value = if (filtro.value == type) null else type
    }
}
