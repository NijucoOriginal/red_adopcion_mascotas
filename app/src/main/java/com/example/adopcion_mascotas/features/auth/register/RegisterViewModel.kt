package com.example.adopcion_mascotas.features.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RegisterUiState(
    val correoElectronico: String = "",
    val nombreUsuario: String = "",
    val contrasenia: String = "",
    val confirmarContrasenia: String = "",
    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val correoError: String? = null,
    val usuarioError: String? = null,
    val contraseniaError: String? = null,
    val confirmarError: String? = null,
    val telefonoError: String? = null,
    val isLoading: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onCorreoChange(v: String) = _uiState.update { it.copy(correoElectronico = v, correoError = null) }
    fun onUsuarioChange(v: String) = _uiState.update { it.copy(nombreUsuario = v, usuarioError = null) }
    fun onContraseniaChange(v: String) = _uiState.update { it.copy(contrasenia = v, contraseniaError = null) }
    fun onConfirmarChange(v: String) = _uiState.update { it.copy(confirmarContrasenia = v, confirmarError = null) }
    fun onPrimerNombreChange(v: String) = _uiState.update { it.copy(primerNombre = v) }
    fun onSegundoNombreChange(v: String) = _uiState.update { it.copy(segundoNombre = v) }
    fun onPrimerApellidoChange(v: String) = _uiState.update { it.copy(primerApellido = v) }
    fun onSegundoApellidoChange(v: String) = _uiState.update { it.copy(segundoApellido = v) }
    fun onDireccionChange(v: String) = _uiState.update { it.copy(direccion = v) }
    fun onTelefonoChange(v: String) = _uiState.update { it.copy(telefono = v, telefonoError = null) }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val s = _uiState.value

        val correoError = if (s.correoElectronico.isBlank()) "Ingresa tu correo electrónico" else null
        val usuarioError = if (s.nombreUsuario.isBlank()) "Ingresa un nombre de usuario" else null
        val contraseniaError = if (s.contrasenia.isBlank()) "Ingresa tu contraseña" else null
        val confirmarError = if (s.confirmarContrasenia != s.contrasenia) "Las contraseñas no coinciden" else null

        val hasErrors = listOf(correoError, usuarioError, contraseniaError, confirmarError).any { it != null }

        if (hasErrors) {
            _uiState.update {
                it.copy(
                    correoError = correoError,
                    usuarioError = usuarioError,
                    contraseniaError = contraseniaError,
                    confirmarError = confirmarError
                )
            }
            return
        }

        // Simulación de registro con delay
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            Log.d("RegisterViewModel", "Simulando registro...")
            delay(1500) // simula llamada a servidor
            _uiState.update { it.copy(isLoading = false) }
            Log.d("RegisterViewModel", "Registro simulado exitoso")
            onSuccess() // dispara el callback para navegar
        }
    }
}
