package com.example.adopcion_mascotas.features.recuperacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasCodigoField
import com.example.adopcion_mascotas.core.componetes.HuellasPasswordField
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperacionScreen(
    onNavigateBack: () -> Unit,
    onRecuperacionExitosa: () -> Unit,
    viewModel: RecuperacionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.onMensajeMostrado()
        }
    }

    LaunchedEffect(state.recuperacionExitosa) {
        if (state.recuperacionExitosa) onRecuperacionExitosa()
    }

    // Desde el paso del código, la flecha vuelve al paso del correo
    val onVolver: () -> Unit = {
        if (state.paso == PasoRecuperacion.CODIGO) {
            viewModel.onVolverAlCorreoClick()
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar contraseña", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            when (state.paso) {
                PasoRecuperacion.CORREO -> {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Ingresa el correo con el que te registraste y te enviaremos un código.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))

                    HuellasTextField(
                        value = state.correoElectronico,
                        onValueChange = viewModel::onCorreoChange,
                        label = "Correo electrónico",
                        leadingIcon = Icons.Outlined.Email,
                        error = state.correoError,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = { viewModel.onSolicitarClick() }),
                        enabled = !state.isLoading
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Enviar código",
                        onClick = viewModel::onSolicitarClick,
                        loading = state.isLoading
                    )
                }

                PasoRecuperacion.CODIGO -> {
                    Text(
                        "Ingresa el código",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Enviamos un código de 5 dígitos a ${state.correoElectronico}. Caduca en 10 minutos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))

                    HuellasCodigoField(
                        codigo = state.codigo,
                        onCodigoChange = viewModel::onCodigoChange,
                        error = state.codigoError
                    )
                    Spacer(Modifier.height(16.dp))

                    HuellasPasswordField(
                        value = state.nuevaContrasenia,
                        onValueChange = viewModel::onNuevaContraseniaChange,
                        label = "Nueva contraseña",
                        error = state.nuevaContraseniaError,
                        helper = "Mínimo 8 caracteres y un número"
                    )
                    Spacer(Modifier.height(8.dp))
                    HuellasPasswordField(
                        value = state.confirmarContrasenia,
                        onValueChange = viewModel::onConfirmarChange,
                        label = "Confirmar contraseña",
                        error = state.confirmarError,
                        imeAction = ImeAction.Done
                    )
                    Spacer(Modifier.height(16.dp))

                    PrimaryButton(
                        text = "Cambiar contraseña",
                        onClick = viewModel::onConfirmarClick,
                        loading = state.isLoading
                    )
                    TextButton(
                        onClick = viewModel::onReenviarCodigoClick,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxSize(0f)
                    ) {
                        Text("Reenviar código")
                    }
                }
            }
        }
    }
}