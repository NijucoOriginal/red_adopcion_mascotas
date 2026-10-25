package com.example.adopcion_mascotas.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.IconoPrincipal
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.features.feed.components.PublicacionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    token: String,
    onSesionExpirada: () -> Unit,
    onPublicacionClick: (Long) -> Unit,
    onCrearPublicacion: () -> Unit,
    onPerfilClick: () -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Al volver a la pantalla (primer arranque o regreso desde el detalle),
    // valida la sesión y recalcula la lista
    DisposableEffect(lifecycleOwner, token) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.validarSesion(token)
                viewModel.refrescar()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Si la sesión caduca, se sale del feed
    LaunchedEffect(state.sesionExpirada) {
        if (state.sesionExpirada) onSesionExpirada()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconoPrincipal("Logo huellas", Modifier.size(32.dp))
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

        // El Box es el contenedor que permite usar Modifier.align
        Box(Modifier.fillMaxSize().padding(padding)) {

            // Contenido principal: filtros + lista
            Column(Modifier.fillMaxSize()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CategoriaPublicacion.entries) { categoria ->
                        FilterChip(
                            selected = state.filtroSeleccionado == categoria,
                            onClick = { viewModel.onFilterSelected(token, categoria) },
                            label = { Text(categoria.name) }
                        )
                    }
                }

                when {
                    state.isLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(40.dp))
                    }

                    state.publicaciones.isEmpty() -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay publicaciones para este filtro",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 96.dp // espacio para que el avatar no tape la última tarjeta
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.publicaciones, key = { it.id }) { pub ->
                            PublicacionCard(
                                publicacion = pub,
                                onClick = { onPublicacionClick(pub.id) }
                            )
                        }
                    }
                }
            }

            // Avatar: hijo directo del Box, por eso funciona align
            Surface(
                onClick = onPerfilClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Mi perfil",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}