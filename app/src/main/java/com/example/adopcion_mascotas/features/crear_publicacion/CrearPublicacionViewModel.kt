package com.example.adopcion_mascotas.features.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.adopcion_mascotas.core.util.NuevaMascota
import com.example.adopcion_mascotas.core.util.NuevaVacuna
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.core.util.ResultadoPublicacion
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.TipoAnimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CrearPublicacionUiState(
    val categoria: CategoriaPublicacion = CategoriaPublicacion.ADOPCION,
    val especie: TipoAnimal = TipoAnimal.PERRO,
    val nombreMascota: String = "",
    val altura: String = "",
    val peso: String = "",
    val longitud: String = "",
    val vacunas: List<NuevaVacuna> = emptyList(),
    val nombreVacuna: String = "",
    val dosisVacuna: String = "",
    val ubicacion: String = "",
    val descripcion: String = "",
    val nombreMascotaError: String? = null,
    val medidasError: String? = null,
    val ubicacionError: String? = null,
    val descripcionError: String? = null,
    val isLoading: Boolean = false,
    val comentariosActivados: Boolean = true,
)

class CrearPublicacionViewModel(private val token: String) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearPublicacionUiState())
    val uiState: StateFlow<CrearPublicacionUiState> = _uiState.asStateFlow()

    private val _resultado = MutableStateFlow<ResultadoPublicacion?>(null)
    val resultado: StateFlow<ResultadoPublicacion?> = _resultado.asStateFlow()

    fun onCategoriaChange(categoria: CategoriaPublicacion) =
        _uiState.update { it.copy(categoria = categoria) }

    fun onEspecieChange(especie: TipoAnimal) =
        _uiState.update { it.copy(especie = especie) }

    fun onNombreMascotaChange(value: String) =
        _uiState.update { it.copy(nombreMascota = value, nombreMascotaError = null) }

    fun onAlturaChange(value: String) =
        _uiState.update { it.copy(altura = value, medidasError = null) }

    fun onPesoChange(value: String) =
        _uiState.update { it.copy(peso = value, medidasError = null) }

    fun onLongitudChange(value: String) =
        _uiState.update { it.copy(longitud = value, medidasError = null) }

    fun onNombreVacunaChange(value: String) =
        _uiState.update { it.copy(nombreVacuna = value) }

    fun onDosisVacunaChange(value: String) =
        _uiState.update { it.copy(dosisVacuna = value) }

    fun onComentariosActivadosChange(value: Boolean) =
        _uiState.update { it.copy(comentariosActivados = value) }

    fun onAgregarVacuna() {
        val s = _uiState.value
        if (s.nombreVacuna.isBlank()) return

        _uiState.update {
            it.copy(
                vacunas = it.vacunas + NuevaVacuna(
                    nombre = s.nombreVacuna.trim(),
                    dosis = s.dosisVacuna.trim()
                ),
                nombreVacuna = "",
                dosisVacuna = ""
            )
        }
    }

    fun onQuitarVacuna(index: Int) =
        _uiState.update { it.copy(vacunas = it.vacunas.filterIndexed { i, _ -> i != index }) }

    fun onUbicacionChange(value: String) =
        _uiState.update { it.copy(ubicacion = value, ubicacionError = null) }

    fun onDescripcionChange(value: String) =
        _uiState.update { it.copy(descripcion = value, descripcionError = null) }

    fun onPublishClick() {
        val s = _uiState.value

        val altura = medida(s.altura)
        val peso = medida(s.peso)
        val longitud = medida(s.longitud)

        val nombreError = if (s.nombreMascota.isBlank()) "Ingresa el nombre de la mascota" else null
        val medidasError = if (altura == null || peso == null || longitud == null) {
            "Usa solo números en altura, peso y longitud"
        } else null
        val ubicacionError = if (s.ubicacion.isBlank()) "Ingresa la ubicación" else null
        val descripcionError = if (s.descripcion.trim().length < 20) {
            "La descripción debe tener al menos 20 caracteres"
        } else null

        _uiState.update {
            it.copy(
                nombreMascotaError = nombreError,
                medidasError = medidasError,
                ubicacionError = ubicacionError,
                descripcionError = descripcionError
            )
        }

        val hayErrores = listOf(nombreError, medidasError, ubicacionError, descripcionError)
            .any { it != null }
        if (hayErrores) return

        _uiState.update { it.copy(isLoading = true) }

        val res = PersistenciaSimulada.crearPublicacion(
            token = token,
            categoria = s.categoria,
            descripcion = s.descripcion.trim(),
            nombreUbicacion = s.ubicacion.trim(),
            mascota = NuevaMascota(
                nombre = s.nombreMascota.trim(),
                tipo = s.especie,
                altura = altura ?: 0.0,
                peso = peso ?: 0.0,
                longitud = longitud ?: 0.0,
                vacunas = s.vacunas
            ),
            comentariosActivados = s.comentariosActivados
        )

        _uiState.update { it.copy(isLoading = false) }
        _resultado.value = res
    }

    fun onResultadoConsumido() {
        _resultado.value = null
    }

    // Campo vacío = 0.0; texto no numérico = null (error)
    private fun medida(texto: String): Double? =
        if (texto.isBlank()) 0.0 else texto.replace(',', '.').toDoubleOrNull()
}

class CrearPublicacionViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CrearPublicacionViewModel(token) as T
}