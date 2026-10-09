package com.example.adopcion_mascotas.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.R
import com.example.adopcion_mascotas.core.ui.components.PrimaryButton
import com.example.adopcion_mascotas.core.ui.components.SecondaryButton

@Composable
fun HomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeScreenViewModel = viewModel()
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
            // Ícono verde de la pata como logotipo
            Icon(
                painter = painterResource(id = R.drawable.ic_paw),
                contentDescription = "Logo Huellas",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(state.welcomeMessage, style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            if (state.showTips) {
                Text(
                    "Consejo: explora las publicaciones y marca las que te interesen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
            }

            FeatureRow(Icons.Outlined.Pets, "Mascotas", "Explora perfiles de adopción.")
            FeatureRow(Icons.Outlined.Campaign, "Reportes", "Informa mascotas perdidas o encontradas.")

            Spacer(Modifier.height(32.dp))
            PrimaryButton(text = "Comenzar", onClick = onNavigateToRegister)
            Spacer(Modifier.height(12.dp))
            SecondaryButton(text = "Ya tengo cuenta", onClick = onNavigateToLogin)
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
