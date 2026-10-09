package com.example.adopcion_mascotas.features.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.repository.FakePublicacionRepository
import com.huellas.app.core.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublicacionDetailUiState(
    val publicacion: Publicacion? = null,
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val isInterested: Boolean = false
)

class PublicacionDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicacionDetailUiState())
    val uiState: StateFlow<PublicacionDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadedId: Long? = null

    fun load(publicacionId: Long) {
        if (loadedId == publicacionId) return
        loadedId = publicacionId
        viewModelScope.launch {
            val pub = FakePublicacionRepository.publicaciones.find { it.id == publicacionId }
            _uiState.update { it.copy(publicacion = pub, isLoading = false, notFound = pub == null) }
        }
    }


    fun onInterestClick() {
        val current = _uiState.value
        val pub = current.publicacion ?: return
        val nowInterested = !current.isInterested
        _uiState.update {
            it.copy(
                isInterested = nowInterested,
                publicacion = pub.copy(
                    cantidad_interesa = (pub.cantidad_interesa + if (nowInterested) 1 else -1).coerceAtLeast(0)
                )
            )
        }
    }

    fun onContactClick() {
        _events.trySend(UiEvent.ShowSnackbar("La mensajería estará disponible próximamente"))
    }


}
