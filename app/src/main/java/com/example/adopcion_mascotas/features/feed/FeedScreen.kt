package com.example.adopcion_mascotas.features.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.ui.components.HuellasLogo
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.features.feed.components.PublicacionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onPublicacionClick: (Long) -> Unit,
    onCrearPublicacion: () -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HuellasLogo(size = 32.dp)
                        Text("  Huellas", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearPublicacion,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Publicar") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CategoriaPublicacion.entries) { categoria ->
                    FilterChip(
                        selected = state.filtroSeleccionado == categoria,
                        onClick = { viewModel.onFilterSelected(categoria) },
                        label = { Text(categoria.name) }
                    )
                }
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(40.dp))
                }
                state.publicaciones.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay publicaciones para este filtro", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.publicaciones, key = { it.id }) { pub ->
                        PublicacionCard(publicacion = pub, onClick = { onPublicacionClick(pub.id) })
                    }
                }
            }
        }
    }
}
