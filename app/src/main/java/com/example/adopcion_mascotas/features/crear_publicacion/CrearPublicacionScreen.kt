package com.example.adopcion_mascotas.features.createpost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton
import com.example.adopcion_mascotas.core.util.ResultadoPublicacion
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.TipoAnimal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CrearPublicacionScreen(
    token: String,
    onNavigateBack: () -> Unit,
    onPublicacionCreada: () -> Unit
) {
    val viewModel: CrearPublicacionViewModel = viewModel(
        factory = CrearPublicacionViewModelFactory(token)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val resultado by viewModel.resultado.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(resultado) {
        when (val r = resultado) {
            ResultadoPublicacion.Exito -> {
                viewModel.onResultadoConsumido()
                onPublicacionCreada()
            }
            is ResultadoPublicacion.Error -> {
                viewModel.onResultadoConsumido()
                snackbarHostState.showSnackbar(r.mensaje)
            }
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nueva publicación", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Box(Modifier.navigationBarsPadding().padding(16.dp)) {
                    PrimaryButton(
                        text = "Publicar",
                        onClick = viewModel::onPublishClick,
                        loading = state.isLoading
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GaleriaPlaceholder()

            SeccionCard(titulo = "Tipo de publicación") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoriaPublicacion.entries.forEach { categoria ->
                        FilterChip(
                            selected = state.categoria == categoria,
                            onClick = { viewModel.onCategoriaChange(categoria) },
                            label = { Text(categoria.etiqueta()) }
                        )
                    }
                }
            }

            SeccionCard(titulo = "Tipo de animal") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoAnimal.entries.forEach { especie ->
                        FilterChip(
                            selected = state.especie == especie,
                            onClick = { viewModel.onEspecieChange(especie) },
                            label = { Text(especie.etiqueta()) }
                        )
                    }
                }
            }

            SeccionCard(titulo = "Datos de la mascota") {
                HuellasTextField(
                    value = state.nombreMascota,
                    onValueChange = viewModel::onNombreMascotaChange,
                    label = "Nombre de la mascota *",
                    error = state.nombreMascotaError
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MedidaField(
                        label = "Altura (cm)",
                        value = state.altura,
                        onValueChange = viewModel::onAlturaChange,
                        modifier = Modifier.weight(1f)
                    )
                    MedidaField(
                        label = "Peso (kg)",
                        value = state.peso,
                        onValueChange = viewModel::onPesoChange,
                        modifier = Modifier.weight(1f)
                    )
                    MedidaField(
                        label = "Largo (cm)",
                        value = state.longitud,
                        onValueChange = viewModel::onLongitudChange,
                        modifier = Modifier.weight(1f)
                    )
                }
                state.medidasError?.let { ErrorTexto(it) }
            }

            SeccionCard(titulo = "Vacunas") {
                HuellasTextField(
                    value = state.nombreVacuna,
                    onValueChange = viewModel::onNombreVacunaChange,
                    label = "Nombre de la vacuna"
                )
                HuellasTextField(
                    value = state.dosisVacuna,
                    onValueChange = viewModel::onDosisVacunaChange,
                    label = "Dosis (opcional)"
                )
                OutlinedButton(
                    onClick = viewModel::onAgregarVacuna,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Text("  Agregar vacuna")
                }

                state.vacunas.forEachIndexed { index, vacuna ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(vacuna.nombre, fontWeight = FontWeight.SemiBold)
                            if (vacuna.dosis.isNotBlank()) {
                                Text(
                                    "Dosis: ${vacuna.dosis}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.onQuitarVacuna(index) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Quitar vacuna")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { /* pendiente: carnet */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.AttachFile, contentDescription = null)
                    Text("  Subir carnet de vacunación")
                }
            }

            SeccionCard(titulo = "Ubicación y descripción") {
                HuellasTextField(
                    value = state.ubicacion,
                    onValueChange = viewModel::onUbicacionChange,
                    label = "Ubicación *",
                    leadingIcon = Icons.Outlined.LocationOn,
                    error = state.ubicacionError,
                    helper = "Barrio o zona"
                )
                HuellasTextField(
                    value = state.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = "Descripción *",
                    singleLine = false,
                    minLines = 4,
                    error = state.descripcionError,
                    helper = "Mínimo 20 caracteres",
                    imeAction = ImeAction.Default
                )
            }

            SeccionCard(titulo = "Comentarios") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Permitir comentarios",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            if (state.comentariosActivados) {
                                "Los usuarios podrán comentar"
                            } else {
                                "Nadie podrá comentar en esta publicación"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.comentariosActivados,
                        onCheckedChange = viewModel::onComentariosActivadosChange
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GaleriaPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sube las fotos de tus mascotas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SeccionCard(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}

@Composable
private fun MedidaField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}

@Composable
private fun ErrorTexto(mensaje: String) {
    Text(
        mensaje,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

private fun CategoriaPublicacion.etiqueta(): String = when (this) {
    CategoriaPublicacion.ADOPCION -> "Adopción"
    CategoriaPublicacion.PERDIDOS -> "Perdidos"
    CategoriaPublicacion.ENCONTRADOS -> "Encontrados"
    CategoriaPublicacion.TEMPORALES -> "Temporal"
    CategoriaPublicacion.VETERINARIA -> "Veterinaria"
}

private fun TipoAnimal.etiqueta(): String = when (this) {
    TipoAnimal.PERRO -> "Perro"
    TipoAnimal.GATO -> "Gato"
    TipoAnimal.CONEJOS -> "Conejo"
    TipoAnimal.AVESDOMESTICAS -> "Ave"
    TipoAnimal.RATONESDOMESTICOS -> "Ratón"
    TipoAnimal.REPTIL -> "Reptil"
    TipoAnimal.PEZ -> "Pez"
}