package com.example.adopcion_mascotas.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.adopcion_mascotas.R

/** Logo de Huellas: patita sobre cuadrado redondeado verde. */
@Composable
fun HuellasLogo(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.3f))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_paw),
            contentDescription = "Logo de Huellas",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(size * 0.62f)
        )
    }
}
