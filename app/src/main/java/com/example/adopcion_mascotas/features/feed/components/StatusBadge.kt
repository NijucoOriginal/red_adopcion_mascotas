package com.example.adopcion_mascotas.features.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adopcion_mascotas.core.theme.BadgeAdoptionBg
import com.example.adopcion_mascotas.core.theme.BadgeAdoptionFg
import com.example.adopcion_mascotas.core.theme.BadgeFosterBg
import com.example.adopcion_mascotas.core.theme.BadgeFosterFg
import com.example.adopcion_mascotas.core.theme.BadgeFoundBg
import com.example.adopcion_mascotas.core.theme.BadgeFoundFg
import com.example.adopcion_mascotas.core.theme.BadgeLostBg
import com.example.adopcion_mascotas.core.theme.BadgeLostFg
import com.example.adopcion_mascotas.domain.model.CategoriaPublicacion

private fun CategoriaPublicacion.colors(): Pair<Color, Color> = when (this) {
    CategoriaPublicacion.ADOPCION -> BadgeAdoptionBg to BadgeAdoptionFg
    CategoriaPublicacion.PERDIDOS -> BadgeLostBg to BadgeLostFg
    CategoriaPublicacion.TEMPORALES -> BadgeFosterBg to BadgeFosterFg
    CategoriaPublicacion.ENCONTRADOS -> BadgeFoundBg to BadgeFoundFg
    CategoriaPublicacion.VETERINARIA -> BadgeFoundBg to BadgeFoundFg
}

@Composable
fun StatusBadge(categoria: CategoriaPublicacion, modifier: Modifier = Modifier) {
    val (bg, fg) = categoria.colors()
    Text(
        text = categoria.name,
        color = fg,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp) // primero el relleno...
            .background(bg)                                // ...y luego el fondo, que lo cubre
    )
}