package com.example.adopcion_mascotas.features.publicacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton
import com.example.adopcion_mascotas.domain.model.Publicacion
import com.example.adopcion_mascotas.domain.model.TipoAnimal
import com.example.adopcion_mascotas.features.feed.components.StatusBadge
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionScreen(
    publicacionId: Long,
    token: String,
    onNavigateBack: () -> Unit,
    onSesionExpirada: () -> Unit,
    onVerFotosClick: (Long) -> Unit,
    viewModel: PublicacionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(publicacionId) {
        viewModel.load(publicacionId, token)
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onMensajeMostrado()
        }
    }

    LaunchedEffect(state.sesionExpirada) {
        if (state.sesionExpirada) onSesionExpirada()
    }

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
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            pub == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No encontramos esta publicación",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> PublicacionDetailContent(
                publicacion = pub,
                isInterested = state.isInterested,
                mascotas = state.mascotas,
                comentarios = state.comentarios,
                textoComentario = state.textoComentario,
                comentarioError = state.comentarioError,
                onInterestClick = viewModel::onInterestClick,
                onContactClick = viewModel::onContactClick,
                onVerFotosClick = onVerFotosClick,
                onTextoComentarioChange = viewModel::onTextoComentarioChange,
                onEnviarComentario = viewModel::onEnviarComentario,
                onMeGustaClick = viewModel::onMeGustaClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun PublicacionDetailContent(
    publicacion: Publicacion,
    isInterested: Boolean,
    mascotas: List<MascotaItem>,
    comentarios: List<ComentarioItem>,
    textoComentario: String,
    comentarioError: String?,
    onInterestClick: () -> Unit,
    onContactClick: () -> Unit,
    onVerFotosClick: (Long) -> Unit,
    onTextoComentarioChange: (String) -> Unit,
    onEnviarComentario: () -> Unit,
    onMeGustaClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Pets,
                contentDescription = "Imagen de publicación",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(Modifier.padding(16.dp)) {
            StatusBadge(publicacion.categoria)
            Spacer(Modifier.height(8.dp))

            Text(
                publicacion.descripcion,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${publicacion.nombre_ubicacion} · ${publicacion.fecha_publicacion.format(formatoFecha)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoTile("Interesados", publicacion.cantidad_interesa.toString(), Modifier.weight(1f))
                InfoTile("Estado", publicacion.estado_publicacion.name, Modifier.weight(1f))
                InfoTile("Moderador", if (publicacion.es_de_moderador) "Sí" else "No", Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            Text("Ubicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                "${publicacion.latitud}, ${publicacion.longitud}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onInterestClick,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = if (isInterested) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Icon(
                    if (isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null
                )
                Text(if (isInterested) "  Te interesa" else "  Me interesa")
            }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onContactClick,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Contactar")
            }
        }

        Spacer(Modifier.height(8.dp))
        VacunasSection(
            mascotas = mascotas,
            onVerFotosClick = onVerFotosClick
        )

        Spacer(Modifier.height(16.dp))
        ComentariosSection(
            activados = publicacion.comentarios_activados,
            comentarios = comentarios,
            texto = textoComentario,
            error = comentarioError,
            onTextoChange = onTextoComentarioChange,
            onEnviar = onEnviarComentario,
            onMeGustaClick = onMeGustaClick
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun VacunasSection(
    mascotas: List<MascotaItem>,
    onVerFotosClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Mascotas y vacunas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (mascotas.isEmpty()) {
            Text(
                "Esta publicación no tiene mascotas registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        mascotas.forEach { mascota ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        mascota.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        mascota.tipo.etiqueta(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (mascota.vacunas.isEmpty()) {
                        Text(
                            "Sin vacunas registradas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        mascota.vacunas.forEach { vacuna ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        vacuna.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        if (vacuna.dosis.isBlank()) {
                                            "Dosis no registrada"
                                        } else {
                                            "Dosis: ${vacuna.dosis}"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onVerFotosClick(mascota.id) },
                                    shape = CircleShape
                                ) {
                                    Text("Ver fotos")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComentariosSection(
    activados: Boolean,
    comentarios: List<ComentarioItem>,
    texto: String,
    error: String?,
    onTextoChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onMeGustaClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Comentarios (${comentarios.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!activados) {
            Text(
                "Los comentarios están desactivados en esta publicación",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        HuellasTextField(
            value = texto,
            onValueChange = onTextoChange,
            label = "Escribe un comentario",
            error = error,
            singleLine = false,
            minLines = 2,
            imeAction = ImeAction.Default
        )
        PrimaryButton(text = "Enviar comentario", onClick = onEnviar)

        if (comentarios.isEmpty()) {
            Text(
                "Aún no hay comentarios. ¡Sé el primero!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            comentarios.forEach { item ->
                ComentarioCard(item = item, onMeGustaClick = onMeGustaClick)
            }
        }
    }
}

@Composable
private fun ComentarioCard(
    item: ComentarioItem,
    onMeGustaClick: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.autor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.esAdmin) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "Moderador",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(item.cuerpo, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.fecha.format(formatoFecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { onMeGustaClick(item.id) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (item.meGustado) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                ) {
                    Icon(
                        if (item.meGustado) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (item.meGustado) "Quitar me gusta" else "Me gusta",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("  ${item.cantidadMeGusta}")
                }
            }
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

private fun TipoAnimal.etiqueta(): String = when (this) {
    TipoAnimal.PERRO -> "Perro"
    TipoAnimal.GATO -> "Gato"
    TipoAnimal.CONEJOS -> "Conejo"
    TipoAnimal.AVESDOMESTICAS -> "Ave doméstica"
    TipoAnimal.RATONESDOMESTICOS -> "Ratón doméstico"
    TipoAnimal.REPTIL -> "Reptil"
    TipoAnimal.PEZ -> "Pez"
}