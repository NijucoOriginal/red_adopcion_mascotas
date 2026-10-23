package com.example.adopcion_mascotas.core.componetes

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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