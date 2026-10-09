package com.example.adopcion_mascotas.features.postdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.features.feed.components.StatusBadge
import com.example.adopcion_mascotas.core.ui.components.CollectUiEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionDetailScreen(
    publicacionId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PublicacionDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(publicacionId) { viewModel.load(publicacionId) }
    CollectUiEvents(viewModel.events, snackbarHostState)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val pub = state.publicacion
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            pub == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No encontramos esta publicación")
            }
            else -> PublicacionDetailContent(
                publicacion = pub,
                isInterested = state.isInterested,
                onInterestClick = viewModel::onInterestClick,
                onContactClick = viewModel::onContactClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun PublicacionDetailContent(
    publicacion: Publicacion,
    isInterested: Boolean,
    onInterestClick: () -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        AsyncImage(
            model = "https://placekitten.com/400/300", // temporal
            contentDescription = "Imagen de publicación",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        Column(Modifier.padding(16.dp)) {
            StatusBadge(publicacion.categoria)
            Spacer(Modifier.height(8.dp))
            Text(publicacion.descripcion, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "${publicacion.nombre_ubicacion} · ${publicacion.fecha_publicacion}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile("Interesados", publicacion.cantidad_interesa.toString(), Modifier.weight(1f))
                InfoTile("Estado", publicacion.estado_publicacion.name, Modifier.weight(1f))
                InfoTile("Moderador", if (publicacion.es_de_moderador) "Sí" else "No", Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            Text("Ubicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("${publicacion.latitud}, ${publicacion.longitud}", style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onInterestClick,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = if (isInterested)
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                else ButtonDefaults.buttonColors()
            ) {
                Icon(if (isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null)
                Text(if (isInterested) "  Te interesa" else "  Me interesa")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onContactClick,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Contactar") }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}
