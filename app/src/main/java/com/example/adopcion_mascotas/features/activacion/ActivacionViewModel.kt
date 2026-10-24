package com.example.adopcion_mascotas.features.activacion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adopcion_mascotas.core.util.registrarUsuario
import com.example.adopcion_mascotas.core.util.verificarCodigo
import com.example.adopcion_mascotas.domain.model.Usuario
import com.example.adopcion_mascotas.features.register.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivacionUiState(
    val codigo: String = "",
    val codigoCompleto: Boolean = false,
    val codigoError: String? = null,
    val codigoEnviado: Boolean = false,
    val cargando: Boolean = false,

)
{

}

class ActivacionViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(ActivacionUiState())

    val uiState: StateFlow<ActivacionUiState> = _uiState.asStateFlow()


    fun onCodigoChange(nuevo: String) {
        _uiState.update { it.copy(codigo = nuevo) }
    }

    fun onSendClick(correo: String) {
        val codigoIngresado = _uiState.value.codigo

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, codigoError = null) }
            try {
                val valido = verificarCodigo(codigoIngresado, correo)
                if (valido) {
                    Log.i("Usuario activado","Usuario activado con exito")
                    _uiState.update { it.copy(codigoEnviado = true) }
                } else {
                    _uiState.update { it.copy(codigoError = "El código no es correcto") }
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
