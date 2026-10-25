package com.example.adopcion_mascotas.features.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion
import com.example.adopcion_mascotas.features.feed.components.PublicacionCard
import java.time.format.DateTimeFormatter

private val formatoFechaBaneo = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedModScreen(
    token: String,
    onSesionExpirada: () -> Unit,
    onPublicacionClick: (Long) -> Unit,
    onCrearPublicacion: () -> Unit,
    onVerPerfilClick: () -> Unit,
    onVerHistorialClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: FeedModViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val sheetState = rememberModalBottomSheetState()

    var mostrarUsuarios by remember { mutableStateOf(false) }
    var usuarioABanear by remember { mutableStateOf<ItemUsuario?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }

    // Recarga al entrar y al volver a la pantalla
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

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Moderación", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { mostrarUsuarios = true }) {
                        Icon(Icons.Filled.Group, contentDescription = "Usuarios")
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        // El Box permite que el avatar se alinee en la esquina inferior izquierda
        Box(Modifier.fillMaxSize().padding(padding)) {

            Column(Modifier.fillMaxSize()) {

                // Filtro por categoría
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.filtroCategoria == null,
                            onClick = { viewModel.onFiltroCategoria(null) },
                            label = { Text("Todas") }
                        )
                    }
                    items(CategoriaPublicacion.entries) { categoria ->
                        FilterChip(
                            selected = state.filtroCategoria == categoria,
                            onClick = { viewModel.onFiltroCategoria(categoria) },
                            label = { Text(categoria.etiqueta()) }
                        )
                    }
                }

                // Filtro por usuario
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.filtroUsuarioId == null,
                            onClick = { viewModel.onFiltroUsuario(null) },
                            label = { Text("Todos los usuarios") }
                        )
                    }
                    items(state.usuarios, key = { it.id }) { usuario ->
                        FilterChip(
                            selected = state.filtroUsuarioId == usuario.id,
                            onClick = { viewModel.onFiltroUsuario(usuario.id) },
                            label = { Text("@${usuario.nombreUsuario}") }
                        )
                    }
                }

                Box(Modifier.weight(1f).fillMaxWidth()) {
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
                                "No hay publicaciones con estos filtros",
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
            }

            // Avatar con su menú: hijo directo del Box, por eso funciona align
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
                            Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
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
                            viewModel.cerrarSesion()
                            onCerrarSesion()
                        }
                    )
                }
            }
        }
    }

    // Lista de usuarios con acciones de baneo
    if (mostrarUsuarios) {
        ModalBottomSheet(
            onDismissRequest = { mostrarUsuarios = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Usuarios de la plataforma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                state.usuarios.forEach { usuario ->
                    UsuarioModeracionRow(
                        usuario = usuario,
                        onBanear = { usuarioABanear = usuario },
                        onLevantar = {
                            usuario.baneo?.let { viewModel.levantarBaneo(it.id) }
                        }
                    )
                }
            }
        }
    }

    // Diálogo para banear
    usuarioABanear?.let { usuario ->
        BanearDialog(
            usuario = usuario,
            onConfirmar = { titulo, razon, dias ->
                viewModel.banear(usuario.id, titulo, razon, dias)
                usuarioABanear = null
            },
            onCancelar = { usuarioABanear = null }
        )
    }
}

@Composable
private fun UsuarioModeracionRow(
    usuario: ItemUsuario,
    onBanear: () -> Unit,
    onLevantar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "@${usuario.nombreUsuario}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    usuario.nombreCompleto.ifBlank { usuario.correo },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    estadoTexto(usuario),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (usuario.baneado) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            when {
                // Los moderadores no pueden ser baneados
                usuario.esModerador -> Text(
                    "Moderador",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                usuario.baneado -> OutlinedButton(onClick = onLevantar) {
                    Icon(
                        Icons.Filled.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("  Levantar")
                }

                else -> OutlinedButton(
                    onClick = onBanear,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Filled.Block,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("  Banear")
                }
            }
        }
    }
}

@Composable
private fun BanearDialog(
    usuario: ItemUsuario,
    onConfirmar: (titulo: String, razon: String, dias: Long?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf<Long?>(7L) }

    val opcionesDuracion = listOf(
        "1 día" to 1L,
        "7 días" to 7L,
        "30 días" to 30L,
        "Permanente" to null
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Banear a @${usuario.nombreUsuario}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HuellasTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = "Título *"
                )
                HuellasTextField(
                    value = razon,
                    onValueChange = { razon = it },
                    label = "Razón *",
                    singleLine = false,
                    minLines = 3
                )
                Text(
                    "Duración",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opcionesDuracion.forEach { (etiqueta, valor) ->
                        FilterChip(
                            selected = dias == valor,
                            onClick = { dias = valor },
                            label = { Text(etiqueta) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(titulo, razon, dias) }) {
                Text("Banear", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

private fun estadoTexto(usuario: ItemUsuario): String {
    val baneo = usuario.baneo
        ?: return if (usuario.esModerador) "Moderador" else "Activo"

    return if (baneo.fecha_fin.year >= 9999) {
        "Baneado permanentemente"
    } else {
        "Baneado hasta ${baneo.fecha_fin.format(formatoFechaBaneo)}"
    }
}

private fun CategoriaPublicacion.etiqueta(): String = when (this) {
    CategoriaPublicacion.ADOPCION -> "Adopción"
    CategoriaPublicacion.PERDIDOS -> "Perdidos"
    CategoriaPublicacion.ENCONTRADOS -> "Encontrados"
    CategoriaPublicacion.TEMPORALES -> "Temporal"
    CategoriaPublicacion.VETERINARIA -> "Veterinaria"
}