package com.example.adopcion_mascotas.features.register

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.core.componetes.ConfirmAlertDialog
import com.example.adopcion_mascotas.core.componetes.HuellasPasswordField
import com.example.adopcion_mascotas.core.componetes.HuellasTextField
import com.example.adopcion_mascotas.core.componetes.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateToBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegistroViewModel = viewModel(),
    onNavigateToActivacion: (correo: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    BackHandler(enabled = !state.mensajeSalida) {
        viewModel.mostrarSalidaClick()
    }

    LaunchedEffect(state.usuarioRegistradoExitosamente) {
        when (state.usuarioRegistradoExitosamente) {
            true -> {
                snackbarHostState.showSnackbar("Cuenta creada con éxito, accediendo a la activación")
                viewModel.resetRegistroResultado()
            }
            false -> {
                snackbarHostState.showSnackbar("El usuario ya se encuentra registrado")
                viewModel.resetRegistroResultado()
            }
            null -> {}
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateToBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Únete a la comunidad Huellas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "Regístrate para adoptar, publicar reportes y proteger a los peludos de tu comunidad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            SectionCard(title = "Datos obligatorios") {
                HuellasTextField(
                    value = state.correo_electronico,
                    onValueChange = viewModel::onCorreoChange,
                    label = "Correo electrónico *",
                    leadingIcon = Icons.Outlined.Email,
                    error = state.correoError,
                    helper = "Recibirás tu código de verificación aquí",
                    keyboardType = KeyboardType.Email
                )
                HuellasTextField(
                    value = state.nombre_usuario,
                    onValueChange = viewModel::onUsuarioChange,
                    label = "Nombre de usuario *",
                    leadingIcon = Icons.Outlined.AlternateEmail,
                    error = state.nombre_usuarioError,
                    helper = "Único en la red Huellas, mín. 4 caracteres"
                )
                HuellasPasswordField(
                    value = state.contrasenia,
                    onValueChange = viewModel::onContraseniaChange,
                    label = "Contraseña *",
                    error = state.contraseniaError,
                    helper = "Mínimo 8 caracteres, al menos 1 número"
                )
                HuellasPasswordField(
                    value = state.confirmarContrasenia,
                    onValueChange = viewModel::onConfirmarChange,
                    label = "Confirmar contraseña *",
                    error = state.confirmarContraseniaError
                )
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "Información complementaria (opcional)") {
                HuellasTextField(
                    value = state.primer_nombre,
                    onValueChange = viewModel::onPrimerNombreChange,
                    label = "Primer nombre",
                    leadingIcon = Icons.Outlined.Badge
                )
                HuellasTextField(
                    value = state.segundo_nombre,
                    onValueChange = viewModel::onSegundoNombreChange,
                    label = "Segundo nombre",
                    leadingIcon = Icons.Outlined.Badge
                )
                HuellasTextField(
                    value = state.primer_apellido,
                    onValueChange = viewModel::onPrimerApellidoChange,
                    label = "Primer apellido",
                    leadingIcon = Icons.Outlined.Badge
                )
                HuellasTextField(
                    value = state.segundo_apellido,
                    onValueChange = viewModel::onSegundoApellidoChange,
                    label = "Segundo apellido",
                    leadingIcon = Icons.Outlined.Badge
                )
                HuellasTextField(
                    value = state.direccion,
                    onValueChange = viewModel::onDireccionChange,
                    label = "Dirección",
                    leadingIcon = Icons.Outlined.LocationOn
                )
                HuellasTextField(
                    value = state.telefono,
                    onValueChange = viewModel::onTelefonoChange,
                    label = "Teléfono de contacto",
                    leadingIcon = Icons.Outlined.Phone,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            }

            Spacer(Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Estado pendiente de activación", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Text(
                        "Al registrarte, tu cuenta quedará pendiente hasta que verifiques tu identidad con el código que enviaremos a tu correo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "Crear cuenta",
                onClick = { viewModel.onRegistroClick(onNavigateToActivacion) },
                loading = state.cargando
            )
            LoginLink(onNavigateToLogin)
            Spacer(Modifier.height(16.dp))
        }
    }

    if(state.mensajeSalida)
    {
        ConfirmAlertDialog(title = "Salir",message = "¿Está seguro que desea salir?",
            { viewModel.confirmarSalida() },
            { viewModel.noMostrarSalidaClick() }, confirmText = "Confirmar", dismissText ="Cancelar")
    }
}

@Composable
private fun LoginLink(onNavigateToLogin: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onNavigateToLogin) { Text("¿Ya tienes una cuenta? Iniciar sesión") }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}