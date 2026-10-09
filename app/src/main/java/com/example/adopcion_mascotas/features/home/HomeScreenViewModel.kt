package com.example.adopcion_mascotas.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val isLoading: Boolean = false,
    val welcomeMessage: String = "Bienvenido a Huellas",
    val showTips: Boolean = true
)

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun toggleTips() {
        _uiState.update { it.copy(showTips = !it.showTips) }
    }

    fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    fun updateWelcomeMessage(message: String) {
        _uiState.update { it.copy(welcomeMessage = message) }
    }
}
