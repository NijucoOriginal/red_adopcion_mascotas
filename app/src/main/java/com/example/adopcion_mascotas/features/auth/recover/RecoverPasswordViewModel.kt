package com.example.adopcion_mascotas.features.auth.recover

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecoverUiState(
    val correoElectronico: String = "",
    val correoError: String? = null,
    val isLoading: Boolean = false,
    val instruccionesEnviadas: Boolean = false
)

class RecoverPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverUiState())
    val uiState: StateFlow<RecoverUiState> = _uiState.asStateFlow()

    fun onCorreoChange(value: String) =
        _uiState.update { it.copy(correoElectronico = value, correoError = null) }

    fun onSendClick() {
        val s = _uiState.value
        if (s.correoElectronico.isBlank()) {
            _uiState.update { it.copy(correoError = "Ingresa tu correo electrónico") }
            return
        }
        // Simulación: marcamos que se enviaron las instrucciones
        _uiState.update { it.copy(instruccionesEnviadas = true) }
    }
}
