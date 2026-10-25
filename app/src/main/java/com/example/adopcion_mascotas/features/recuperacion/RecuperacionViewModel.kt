package com.example.adopcion_mascotas.features.recuperacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.Resultado
import com.example.adopcion_mascotas.core.util.UsuarioValidators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PasoRecuperacion { CORREO, CODIGO }

data class RecuperacionUiState(
    val paso: PasoRecuperacion = PasoRecuperacion.CORREO,
    val correoElectronico: String = "",
    val correoError: String? = null,
    val codigo: String = "",
    val codigoError: String? = null,
    val nuevaContrasenia: String = "",
    val nuevaContraseniaError: String? = null,
    val confirmarContrasenia: String = "",
    val confirmarError: String? = null,
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val recuperacionExitosa: Boolean = false
)

class RecuperacionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperacionUiState())
    val uiState: StateFlow<RecuperacionUiState> = _uiState.asStateFlow()

    fun onCorreoChange(value: String) =
        _uiState.update { it.copy(correoElectronico = value, correoError = null) }

    fun onCodigoChange(value: String) =
        _uiState.update {
            it.copy(codigo = value.filter { c -> c.isDigit() }.take(5), codigoError = null)
        }

    fun onNuevaContraseniaChange(value: String) =
        _uiState.update { it.copy(nuevaContrasenia = value, nuevaContraseniaError = null) }

    fun onConfirmarChange(value: String) =
        _uiState.update { it.copy(confirmarContrasenia = value, confirmarError = null) }

    // Paso 1: validar el correo y pedir el código
    fun onSolicitarClick() {
        val correo = _uiState.value.correoElectronico.trim()
        val error = UsuarioValidators.correoElectronico(correo)
        if (error != null) {
            _uiState.update { it.copy(correoError = error) }
            return
        }
        enviarCodigo(correo, avanzarAlCodigo = true)
    }

    fun onReenviarCodigoClick() {
        enviarCodigo(_uiState.value.correoElectronico.trim(), avanzarAlCodigo = false)
    }

    private fun enviarCodigo(correo: String, avanzarAlCodigo: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800) // simula la espera del servidor
            PersistenciaSimulada.solicitarCodigoRecuperacion(correo)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    correoElectronico = correo,
                    paso = if (avanzarAlCodigo) PasoRecuperacion.CODIGO else it.paso,
                    codigo = "",
                    codigoError = null,
                    mensaje = "Si el correo está registrado, te enviamos un código de 5 dígitos"
                )
            }
        }
    }

    fun onVolverAlCorreoClick() {
        _uiState.update {
            it.copy(paso = PasoRecuperacion.CORREO, codigo = "", codigoError = null)
        }
    }

    // Paso 2: validar el código y la nueva contraseña, y cambiarla
    fun onConfirmarClick() {
        val s = _uiState.value

        val codigoError = if (s.codigo.length == 5) null else "Ingresa los 5 dígitos del código"
        val contraseniaError = UsuarioValidators.contrasenia(s.nuevaContrasenia)
        val confirmarError = UsuarioValidators.confirmarContrasenia(
            s.nuevaContrasenia,
            s.confirmarContrasenia
        )

        _uiState.update {
            it.copy(
                codigoError = codigoError,
                nuevaContraseniaError = contraseniaError,
                confirmarError = confirmarError
            )
        }
        if (codigoError != null || contraseniaError != null || confirmarError != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            val resultado = PersistenciaSimulada.cambiarContraseniaConCodigo(
                correo = s.correoElectronico.trim(),
                codigo = s.codigo,
                nuevaContrasenia = s.nuevaContrasenia
            )
            _uiState.update {
                when (resultado) {
                    is Resultado.Exito -> it.copy(
                        isLoading = false,
                        recuperacionExitosa = true,
                        mensaje = "Contraseña actualizada. Inicia sesión"
                    )
                    is Resultado.Error -> it.copy(
                        isLoading = false,
                        codigoError = resultado.mensaje
                    )
                    Resultado.SesionExpirada -> it.copy(
                        isLoading = false,
                        codigoError = "Solicita un nuevo código"
                    )
                }
            }
        }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }
}