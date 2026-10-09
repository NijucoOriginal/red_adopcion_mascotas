package com.example.adopcion_mascotas.features.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correoElectronico: String = "",
    val contrasenia: String = "",
    val correoError: String? = null,
    val contraseniaError: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(value: String) =
        _uiState.update { it.copy(correoElectronico = value, correoError = null) }

    fun onContraseniaChange(value: String) =
        _uiState.update { it.copy(contrasenia = value, contraseniaError = null) }

    fun onLoginClick(onSuccess: () -> Unit) {
        val s = _uiState.value
        val correoError = if (s.correoElectronico.isBlank()) "Ingresa tu correo electrónico" else null
        val contraseniaError = if (s.contrasenia.isBlank()) "Ingresa tu contraseña" else null

        if (correoError != null || contraseniaError != null) {
            _uiState.update { it.copy(correoError = correoError, contraseniaError = contraseniaError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            Log.d("LoginViewModel", "Simulando login...")
            delay(1500) // simula llamada a servidor
            _uiState.update { it.copy(isLoading = false) }
            Log.d("LoginViewModel", "Login simulado exitoso")
            onSuccess()
        }
    }
}

