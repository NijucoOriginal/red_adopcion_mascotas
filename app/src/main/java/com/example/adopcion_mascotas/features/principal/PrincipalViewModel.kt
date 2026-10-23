package com.example.adopcion_mascotas.features.principal

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

data class PrincipalUiState(
    val cargando: String,
    val mensajeBienvenida: String = "Bienvenido a Huellas"
)

class PrincipalViewModel(): ViewModel() {


}