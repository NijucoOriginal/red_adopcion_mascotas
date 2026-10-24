package com.example.adopcion_mascotas.features.activacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasCodigoField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivacionScreen(
    onNavigateToBack: () -> Unit,
    viewModel: ActivacionViewModel = viewModel(),
    correoElectronico: String,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar de éxito: se muestra cuando el código queda verificado
    LaunchedEffect(state.codigoEnviado) {
        if (state.codigoEnviado) {
            snackbarHostState.showSnackbar(
                "Código enviado correctamente. Ya puedes acceder a nuestros servicios."
            )
        }
    }

    // Snackbar de error: se muestra cuando el código es incorrecto
    LaunchedEffect(state.codigoError) {
        if (state.codigoError != null) {
            snackbarHostState.showSnackbar("El código no es correcto")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Activar cuenta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToBack) {
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
            Text(
                "Ingresa tu código de activación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Ingresa el código que enviamos a tu correo.",
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

            // El botón siempre está visible
            PrimaryButton(
                text = "Verificar código",
                onClick = { viewModel.onSendClick(correoElectronico) },
                loading = state.cargando
            )

            // Texto informativo: aparece debajo del botón, sin reemplazarlo
            if (state.codigoEnviado) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tu cuenta ya fue verificada. Redirigiendo al login",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                onNavigateToLogin()
            }
        }
    }
}