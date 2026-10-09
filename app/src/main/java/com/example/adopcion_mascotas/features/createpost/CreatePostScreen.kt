package com.example.adopcion_mascotas.features.createpost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.adopcion_mascotas.core.ui.components.HuellasTextField
import com.example.adopcion_mascotas.core.ui.components.PrimaryButton
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.domain.model.TipoAnimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel = viewModel(),
    onPostCreated: () -> Boolean
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = state.imagenUrl,
                contentDescription = "Imagen de la publicación (temporal)",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            OutlinedButton(onClick = viewModel::onChangeImage, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Text("  Cambiar imagen aleatoria")
            }

            Spacer(Modifier.height(4.dp))
            Text("Tipo de publicación", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoriaPublicacion.entries.forEach { t ->
                    FilterChip(selected = state.categoria == t, onClick = { viewModel.onCategoriaChange(t) }, label = { Text(t.name) })
                }
            }

            Text("Tipo de animal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoAnimal.entries.forEach { sp ->
                    FilterChip(selected = state.especie == sp, onClick = { viewModel.onEspecieChange(sp) }, label = { Text(sp.name) })
                }
            }

            HuellasTextField(
                value = state.nombreMascota,
                onValueChange = viewModel::onNombreMascotaChange,
                label = "Nombre de la mascota *",
                leadingIcon = Icons.Outlined.Pets,
                error = state.nombreMascotaError
            )
            HuellasTextField(
                value = state.altura,
                onValueChange = viewModel::onAlturaChange,
                label = "Altura (cm)"
            )
            HuellasTextField(
                value = state.peso,
                onValueChange = viewModel::onPesoChange,
                label = "Peso (kg)"
            )
            HuellasTextField(
                value = state.longitud,
                onValueChange = viewModel::onLongitudChange,
                label = "Longitud (cm)"
            )
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

            Spacer(Modifier.height(8.dp))
            PrimaryButton(text = "Publicar", onClick = viewModel::onPublishClick, loading = state.isLoading)
            Spacer(Modifier.height(16.dp))
        }
    }
}
