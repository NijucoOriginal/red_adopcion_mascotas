package com.example.adopcion_mascotas.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.util.PersistenciaSimulada
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.EstadoPublicacion
import com.example.adopcion_mascotas.features.feed.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPublicacionesScreen(
    token: String,
    onNavigateBack: () -> Unit,
    onSesionExpirada: () -> Unit,
    onModificarClick: (Long) -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Carga la lista al entrar y la vuelve a cargar al regresar de la edición.
    // Usa load(token) y no cargar() para que el token ya esté guardado.
    DisposableEffect(lifecycleOwner, token) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load(token)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.sesionExpirada) {
        if (state.sesionExpirada) onSesionExpirada()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis publicaciones", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
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

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
            }

            state.publicaciones.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aún no has creado publicaciones",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.publicaciones, key = { it.id }) { item ->
                    ItemHistorialCard(item = item, onModificarClick = onModificarClick)
                }
            }
        }
    }
}

@Composable
private fun ItemHistorialCard(
    item: ItemHistorial,
    onModificarClick: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(item.categoria)
                if (item.oculta) {
                    Spacer(Modifier.height(0.dp))
                    Spacer(Modifier.height(0.dp))
                    EtiquetaInfo("Oculta", MaterialTheme.colorScheme.error)
                }
            }

            Text(
                item.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Text(
                "Estado: ${item.estado.etiquetaEstado()} · Interesados: ${item.interesados}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!item.comentariosActivados) {
                Text(
                    "Comentarios desactivados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = { onModificarClick(item.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modificar")
            }
        }
    }
}

@Composable
private fun EtiquetaInfo(texto: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

// Etiquetas compartidas con la pantalla de edición (mismo paquete)
internal fun EstadoPublicacion.etiquetaEstado(): String = when (this) {
    EstadoPublicacion.PENDIENTE -> "Pendiente"
    EstadoPublicacion.RECHAZADO -> "Rechazada"
    EstadoPublicacion.ADMITIDO -> "Admitida"
    EstadoPublicacion.RESUELTO -> "Resuelta"
}

internal fun CategoriaPublicacion.etiquetaCategoria(): String = when (this) {
    CategoriaPublicacion.ADOPCION -> "Adopción"
    CategoriaPublicacion.PERDIDOS -> "Perdidos"
    CategoriaPublicacion.ENCONTRADOS -> "Encontrados"
    CategoriaPublicacion.TEMPORALES -> "Temporal"
    CategoriaPublicacion.VETERINARIA -> "Veterinaria"
}