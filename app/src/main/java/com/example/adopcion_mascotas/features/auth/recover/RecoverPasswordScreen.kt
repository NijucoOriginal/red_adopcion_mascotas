package com.example.adopcion_mascotas.features.auth.recover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.ui.components.HuellasLogo
import com.example.adopcion_mascotas.core.ui.components.*
import com.example.adopcion_mascotas.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecoverPasswordViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar contraseña", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
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
            Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Ingresa el correo con el que te registraste y te mostraremos un mensaje de confirmación.",
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
                keyboardActions = KeyboardActions(onDone = { viewModel.onSendClick() }),
                enabled = !state.instruccionesEnviadas
            )
            Spacer(Modifier.height(16.dp))

            if (state.instruccionesEnviadas) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Revisa tu bandeja de entrada", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Si el correo está registrado, recibirás las instrucciones en unos minutos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                SecondaryButton(text = "Volver a iniciar sesión", onClick = onNavigateBack)
            } else {
                PrimaryButton(text = "Enviar instrucciones", onClick = viewModel::onSendClick, loading = state.isLoading)
            }
        }
    }
}
