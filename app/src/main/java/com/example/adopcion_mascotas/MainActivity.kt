package com.example.adopcion_mascotas

import android.util.Log

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.adopcion_mascotas.core.theme.Adopcion_mascotasTheme
import com.example.adopcion_mascotas.features.principal.PrincipalScreen
import com.example.adopcion_mascotas.features.principal.PrincipalViewModel
import com.example.adopcion_mascotas.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Greeting", "Greeting function called with name: NicolasAndroid")
        Log.i("Greeting", "Greeting function called with name: NicolasAndroid")

        enableEdgeToEdge()
        setContent {
            Adopcion_mascotasTheme {
                AppNavigation()
            }
        }
    }
}
