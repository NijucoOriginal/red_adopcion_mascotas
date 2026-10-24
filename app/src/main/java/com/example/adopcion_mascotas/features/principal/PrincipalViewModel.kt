package com.example.adopcion_mascotas.features.principal

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrincipalUiState(
    val cargando: Boolean = false
)

class PrincipalViewModel(): ViewModel() {

    private val _uiState = MutableStateFlow(PrincipalUiState())
    val uiState: StateFlow<PrincipalUiState> = _uiState.asStateFlow()

    fun ponerCargando(estaCargando: Boolean) {

    }

}