package com.example.adopcion_mascotas.features.createpost

import androidx.lifecycle.ViewModel
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.adopcion_mascotas.domain.model.TipoAnimal

data class CreatePostUiState(
    val imagenUrl: String = "",
    val categoria: CategoriaPublicacion = CategoriaPublicacion.ADOPCION,
    val especie: TipoAnimal = TipoAnimal.PERRO,
    val nombreMascota: String = "",
    val altura: String = "",
    val peso: String = "",
    val longitud: String = "",
    val ubicacion: String = "",
    val descripcion: String = "",
    val nombreMascotaError: String? = null,
    val ubicacionError: String? = null,
    val descripcionError: String? = null,
    val isLoading: Boolean = false
)

class CreatePostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun onChangeImage() =
        _uiState.update { it.copy(imagenUrl = "https://placekitten.com/400/300") } // simulación

    fun onCategoriaChange(categoria: CategoriaPublicacion) =
        _uiState.update { it.copy(categoria = categoria) }

    fun onEspecieChange(especie: TipoAnimal) =
        _uiState.update { it.copy(especie = especie) }

    fun onNombreMascotaChange(value: String) =
        _uiState.update { it.copy(nombreMascota = value, nombreMascotaError = null) }

    fun onAlturaChange(value: String) =
        _uiState.update { it.copy(altura = value) }

    fun onPesoChange(value: String) =
        _uiState.update { it.copy(peso = value) }

    fun onLongitudChange(value: String) =
        _uiState.update { it.copy(longitud = value) }

    fun onUbicacionChange(value: String) =
        _uiState.update { it.copy(ubicacion = value, ubicacionError = null) }

    fun onDescripcionChange(value: String) =
        _uiState.update { it.copy(descripcion = value, descripcionError = null) }

    fun onPublishClick() {
        val s = _uiState.value
        val petError = if (s.nombreMascota.isBlank()) "Ingresa el nombre de la mascota" else null
        val locError = if (s.ubicacion.isBlank()) "Ingresa la ubicación" else null
        val descError = if (s.descripcion.length < 20) "La descripción debe tener al menos 20 caracteres" else null

        _uiState.update {
            it.copy(
                nombreMascotaError = petError,
                ubicacionError = locError,
                descripcionError = descError
            )
        }
    }
}
