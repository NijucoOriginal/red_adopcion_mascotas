package com.example.adopcion_mascotas.features.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada.registrarUsuario
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada.generarCodigoUnico
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada.verificarCodigo
import com.example.adopcion_mascotas.domain.model.EstadoUsuario
import com.example.adopcion_mascotas.domain.model.Rol
import com.example.adopcion_mascotas.features.principal.PrincipalUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre_usuario: String ="",
    val correo_electronico: String ="",
    val contrasenia: String ="",
    val confirmarContrasenia: String ="",
    val rol: Rol =Rol.USUARIO,
    var codigo_multiproposito: String ="",
    var primer_nombre: String ="",
    var segundo_nombre: String ="",
    var primer_apellido: String ="",
    var segundo_apellido: String ="",
    var direccion: String ="",
    var telefono: String ="",
    var estado_usuario: EstadoUsuario = EstadoUsuario.PENDIENTE,
    val mensajeSalida: Boolean = false,

    val correoError: String? = null,
    val nombre_usuarioError: String? = null,
    val contraseniaError: String? = null,
    val confirmarContraseniaError: String? = null,
    val cargando: Boolean =false,
    val usuarioRegistradoExitosamente: Boolean? = null
) {

}

class RegistroViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(RegistroUiState())

    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun mostrarSalidaClick() {
        _uiState.update { it.copy(mensajeSalida = true) }
    }

    fun noMostrarSalidaClick() {
        _uiState.update { it.copy(mensajeSalida = false) }
    }

    fun confirmarSalida() {
        _uiState.value= RegistroUiState()
    }

    fun onCorreoChange(correNuevo: String) {
        _uiState.update { it.copy(correo_electronico = correNuevo) }
    }

    fun onUsuarioChange(nombreNuevo: String) {
        _uiState.update { it.copy(nombre_usuario = nombreNuevo) }
    }

    fun onContraseniaChange(contraseniaNueva: String) {
        _uiState.update { it.copy(contrasenia = contraseniaNueva) }
    }

    fun onConfirmarChange(confirmarContraseniaNueva: String) {
        _uiState.update { it.copy(confirmarContrasenia = confirmarContraseniaNueva) }
    }

    fun onPrimerNombreChange(nuevoPrimerNombre: String) {
        _uiState.update { it.copy(primer_nombre = nuevoPrimerNombre) }

    }

    fun onSegundoNombreChange(nuevoSegundoNombre: String) {
        _uiState.update { it.copy(segundo_nombre = nuevoSegundoNombre) }

    }

    fun onPrimerApellidoChange(nuevoPrimerApellido: String) {
        _uiState.update { it.copy(primer_apellido = nuevoPrimerApellido) }

    }

    fun onSegundoApellidoChange(nuevoPrimerApellido: String) {
        _uiState.update { it.copy(segundo_apellido = nuevoPrimerApellido) }

    }


    fun onDireccionChange(nuevaDireccion: String) {
        _uiState.update { it.copy(direccion = nuevaDireccion) }
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        _uiState.update { it.copy(telefono = nuevoTelefono) }
    }

    fun onRegistroClick(onNavigateToActivacion: (String) -> Unit) {
        _uiState.update { it.copy(cargando = true) }
        val s = _uiState.value

        val correoError =
            if (s.correo_electronico.isBlank()) "Ingresa tu correo electrónico" else null
        val usuarioError = if (s.nombre_usuario.isBlank()) "Ingresa un nombre de usuario" else null
        val contraseniaError = if (s.contrasenia.isBlank()) "Ingresa tu contraseña" else null
        val confirmarError =
            if (s.confirmarContrasenia != s.contrasenia) "Las contraseñas no coinciden" else null

        val hasErrors =
            listOf(correoError, usuarioError, contraseniaError, confirmarError).any { it != null }

        if (hasErrors) {
            _uiState.update { it.copy(correoError = correoError,
                nombre_usuarioError = usuarioError,
                contraseniaError = contraseniaError,
                confirmarContraseniaError = confirmarError, cargando = false) }
            return
        }

        val codigo = generarCodigoUnico()
        Log.i(
            "ENVIO_CODIGO",
            "Codigo multiproposito enviado exitosamente: $codigo"
        )

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val sePudoRegistrar = registrarUsuario(
                    s.nombre_usuario, s.correo_electronico, s.contrasenia,
                    s.primer_nombre, s.segundo_nombre, s.primer_apellido, s.segundo_apellido,
                    s.direccion, s.telefono, codigo
                )

                _uiState.update { it.copy(usuarioRegistradoExitosamente = sePudoRegistrar) }

                if (sePudoRegistrar) {
                    onNavigateToActivacion(s.correo_electronico)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(usuarioRegistradoExitosamente = false) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun resetRegistroResultado() {
        _uiState.update { it.copy(usuarioRegistradoExitosamente = null) }
    }
}

