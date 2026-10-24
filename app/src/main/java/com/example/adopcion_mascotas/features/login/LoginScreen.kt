package com.example.adopcion_mascotas.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.HuellasPasswordField
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.IconoPrincipal
import com.example.adopcion_mascotas.core.componetes.PrimaryButton
import com.example.adopcion_mascotas.core.componetes.SecondaryButton
import com.example.adopcion_mascotas.domain.model.Rol

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegistro: () -> Unit,
    onNavigateToRecuperacion: () -> Unit,
    onNavigateToFeed: (token: String) -> Unit,
    onNavigateToFeedMod: (token: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(16.dp))
            IconoPrincipal("Logo huellas", modifier = Modifier.size(88.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Huellas",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Red comunitaria para la adopción y protección animal",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            HuellasTextField(
                value = state.correoElectronico,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo electrónico",
                leadingIcon = Icons.Outlined.Person,
                error = state.correoError,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(8.dp))
            HuellasPasswordField(
                value = state.contrasenia,
                onValueChange = viewModel::onContraseniaChange,
                label = "Contraseña",
                error = state.contraseniaError,
                imeAction = ImeAction.Done
            )

            // Mensaje de error del servidor o de la persistencia (credenciales, cuenta inactiva, etc.)
            state.mensajeError?.let { mensaje ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "Iniciar sesión",
                onClick = {
                    viewModel.onLoginClick { token, rol ->
                        when (rol) {
                            Rol.MODERADOR -> onNavigateToFeedMod(token)
                            else -> onNavigateToFeed(token)
                        }
                    }
                },
                loading = state.isLoading
            )

            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "¿Olvidaste tu contraseña?",
                onClick = onNavigateToRecuperacion
            )

            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Crear cuenta",
                onClick = onNavigateToRegistro
            )
        }
    }
}