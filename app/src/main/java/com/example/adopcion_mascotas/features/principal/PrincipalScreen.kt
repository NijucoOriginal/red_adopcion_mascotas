package com.example.adopcion_mascotas.features.principal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adopcion_mascotas.R
import com.example.adopcion_mascotas.core.componetes.IconoPrincipal
import com.example.adopcion_mascotas.core.componetes.PrimaryButton
import com.example.adopcion_mascotas.core.componetes.SecondaryButton
import com.example.adopcion_mascotas.core.theme.Adopcion_mascotasTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adopcion_mascotas.core.componetes.IconoCargando

val consejos: List<String> = listOf(
    "asegúrate de que tu vivienda sea adecuada para el tamaño y las necesidades de la mascota que deseas adoptar.",
    "las mascotas necesitan atención, ejercicio y compañía todos los días.",
    "recuerda que una mascota implica alimentación, vacunas, controles veterinarios y posibles emergencias.",
    "todas las personas del hogar deben estar de acuerdo con la adopción.",
    "es normal que una mascota tarde varios días o semanas en adaptarse a un nuevo entorno.",
    "utiliza el refuerzo positivo y evita cualquier tipo de castigo físico.",
    "realiza un chequeo médico poco después de la adopción.",
    "las mascotas se sienten más seguras cuando tienen horarios estables para comer, jugar y descansar.",
    "las vacunas ayudan a prevenir enfermedades graves.",
    "realiza desparasitaciones internas y externas según las recomendaciones veterinarias.",
    "cada mascota tiene necesidades nutricionales diferentes según su edad y tamaño.",
    "asegúrate de que siempre tenga acceso a agua limpia y fresca.",
    "el ejercicio es fundamental para la salud física y mental.",
    "utiliza placas de identificación o microchip para facilitar su recuperación en caso de pérdida.",
    "recoge siempre los desechos de tu mascota en espacios públicos.",
    "el juego ayuda a fortalecer el vínculo y reducir el estrés.",
    "adoptar es un compromiso para toda la vida."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegistro: () -> Unit,
    viewModel: PrincipalViewModel = viewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()


    Scaffold(containerColor = MaterialTheme.colorScheme.background)
    { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 50.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if(state.cargando)
            {
                IconoCargando("Dirigiendonos a registro",100)
            }
            else
            {
                IconoPrincipal("Logo huellas",Modifier.size(100.dp))


                Text(
                    text = "Bienvenido a huellas",
                    style = MaterialTheme.typography.titleLarge

                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text ="Consejo: "+consejos.random(),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(Modifier.height(16.dp))

                FeatureRow(Icons.Outlined.Pets, "Mascotas", "Explora perfiles de adopción.")
                FeatureRow(Icons.Outlined.Campaign, "Reportes", "Informa mascotas perdidas o encontradas.")

                Spacer(Modifier.height(32.dp))
                PrimaryButton(text = "Comenzar", onClick = onNavigateToRegistro)
                Spacer(Modifier.height(12.dp))
                SecondaryButton(text = "Ya tengo cuenta", onClick = onNavigateToLogin)
            }
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

@Preview(showBackground = true)
@Composable
private fun PrincipalScreenPreview() {
    Adopcion_mascotasTheme {
        PrincipalScreen(
            onNavigateToLogin = {},
            onNavigateToRegistro = {}
        )
    }
}
