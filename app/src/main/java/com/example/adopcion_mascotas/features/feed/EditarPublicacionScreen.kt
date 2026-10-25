package com.example.adopcion_mascotas.features.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import com.example.adopcion_mascotas.features.feed.etiquetaCategoria
import com.example.adopcion_mascotas.features.feed.etiquetaEstado

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditarPublicacionScreen(
    publicacionId: Long,
    token: String,
    onNavigateBack: () -> Unit,
    onSesionExpirada: () -> Unit,
    viewModel: EditarPublicacionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(publicacionId) {
        viewModel.load(publicacionId, token)
    }

    // Al guardar o eliminar, se vuelve al historial, que se recarga solo
    LaunchedEffect(state.guardadoExitoso, state.eliminada) {
        if (state.guardadoExitoso || state.eliminada) onNavigateBack()
    }

    LaunchedEffect(state.sesionExpirada) {
        if (state.sesionExpirada) onSesionExpirada()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Modificar publicación", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (!state.isLoading && !state.notFound) {
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Box(Modifier.navigationBarsPadding().padding(16.dp)) {
                        PrimaryButton(
                            text = "Guardar cambios",
                            onClick = viewModel::onGuardarClick,
                            loading = state.guardando
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.notFound -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.mensaje ?: "No encontramos esta publicación",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Comentarios: se pueden activar o desactivar; los anteriores siguen visibles
                SeccionEditable(titulo = "Comentarios") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Permitir comentarios",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Los comentarios anteriores seguirán visibles",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.comentariosActivados,
                            onCheckedChange = viewModel::onComentariosChange
                        )
                    }
                }

                // Estado: depende del estado actual de la publicación
                SeccionEditable(titulo = "Estado") {
                    when (state.estadoOriginal) {
                        EstadoPublicacion.ADMITIDO -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "Marcar como resuelta",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "Este cambio no se puede revertir",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.marcarResuelta,
                                    onCheckedChange = viewModel::onMarcarResueltaChange
                                )
                            }
                        }
                        EstadoPublicacion.RECHAZADO -> {
                            Text(
                                "Un moderador rechazó esta publicación. Puedes corregirla y volver a enviarla a revisión.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            OutlinedButton(
                                onClick = viewModel::onReenviarClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volver a enviar a revisión")
                            }
                        }
                        EstadoPublicacion.PENDIENTE -> {
                            Text(
                                "Pendiente de revisión por un moderador",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        else -> {
                            Text(
                                "Estado actual: ${state.estadoOriginal?.etiquetaEstado() ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                SeccionEditable(titulo = "Descripción") {
                    HuellasTextField(
                        value = state.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        label = "Descripción *",
                        singleLine = false,
                        minLines = 4,
                        error = state.descripcionError,
                        helper = "Mínimo 20 caracteres"
                    )
                }

                SeccionEditable(titulo = "Categoría") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoriaPublicacion.entries.forEach { categoria ->
                            FilterChip(
                                selected = state.categoria == categoria,
                                onClick = { viewModel.onCategoriaChange(categoria) },
                                label = { Text(categoria.etiquetaCategoria()) }
                            )
                        }
                    }
                }

                SeccionEditable(titulo = "Ubicación") {
                    HuellasTextField(
                        value = state.nombreUbicacion,
                        onValueChange = viewModel::onNombreUbicacionChange,
                        label = "Nombre de la ubicación *",
                        error = state.ubicacionError,
                        helper = "Barrio o zona"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HuellasTextField(
                            value = state.latitud,
                            onValueChange = viewModel::onLatitudChange,
                            label = "Latitud",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        HuellasTextField(
                            value = state.longitud,
                            onValueChange = viewModel::onLongitudChange,
                            label = "Longitud",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    state.coordenadasError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                SeccionEditable(titulo = "Visibilidad") {
                    OutlinedButton(
                        onClick = viewModel::onAlternarOcultarClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.oculta) "Mostrar publicación" else "Ocultar publicación")
                    }
                    Text(
                        if (state.oculta) {
                            "Actualmente no aparece en el feed"
                        } else {
                            "Puedes ocultarla del feed sin eliminarla"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SeccionEditable(titulo = "Zona de peligro") {
                    OutlinedButton(
                        onClick = { confirmarEliminar = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar publicación")
                    }
                }

                state.mensaje?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar publicación?") },
            text = {
                Text(
                    "Se borrarán la publicación, su mascota, sus vacunas y sus comentarios. Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        viewModel.onEliminarConfirmado()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SeccionEditable(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
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