package com.example.adopcion_mascotas.features.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.ResultadoLogin
import com.example.adopcion_mascotas.domain.model.Rol
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
    val mensajeError: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(value: String) =
        _uiState.update { it.copy(correoElectronico = value, correoError = null) }

    fun onContraseniaChange(value: String) =
        _uiState.update { it.copy(contrasenia = value, contraseniaError = null) }

    fun onLoginClick(onLoginExitoso: (token: String, rol: Rol) -> Unit) {
        val s = _uiState.value
        val correoError = if (s.correoElectronico.isBlank()) "Ingresa tu correo electrónico" else null
        val contraseniaError = if (s.contrasenia.isBlank()) "Ingresa tu contraseña" else null

        if (correoError != null || contraseniaError != null) {
            _uiState.update { it.copy(correoError = correoError, contraseniaError = contraseniaError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                when (val resultado = PersistenciaSimulada.login(s.correoElectronico, s.contrasenia)) {
                    is ResultadoLogin.Exito -> onLoginExitoso(resultado.token, resultado.rol)
                    is ResultadoLogin.Error -> _uiState.update { it.copy(mensajeError = resultado.mensaje) }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }

        }
    }
}
