package com.example.adopcion_mascotas.core.componetes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.adopcion_mascotas.R

@Composable
fun IconoPrincipal(descripcion: String,modifier: Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.logo_huellas),
        contentDescription = descripcion,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )

}

@Composable
fun IconoCargando(descripcion: String,tamanoCircular: Int) {
    CircularProgressIndicator(
        modifier = Modifier.size(tamanoCircular.dp),
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.onPrimary
    )
    Spacer(Modifier.padding(16.dp))
    Text(text = descripcion, style = MaterialTheme.typography.titleLarge )
}