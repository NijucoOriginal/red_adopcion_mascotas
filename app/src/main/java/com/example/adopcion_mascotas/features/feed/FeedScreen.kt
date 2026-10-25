package com.example.adopcion_mascotas.features.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    onVerPerfilClick: () -> Unit,
    onVerHistorialClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var menuAbierto by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        viewModel.validarSesion(token)
    }

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

        Box(Modifier.fillMaxSize().padding(padding)) {

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
                            bottom = 96.dp
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

            // Avatar con su menú. El menú se abre encima del botón.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    onClick = { menuAbierto = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Mi perfil",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver perfil") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        onClick = {
                            menuAbierto = false
                            onVerPerfilClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ver historial de publicaciones") },
                        leadingIcon = { Icon(Icons.Filled.History, contentDescription = null) },
                        onClick = {
                            menuAbierto = false
                            onVerHistorialClick()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Cerrar sesión",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuAbierto = false
                            viewModel.cerrarSesion(token)
                            onCerrarSesion()
                        }
                    )
                }
            }
        }
    }
}