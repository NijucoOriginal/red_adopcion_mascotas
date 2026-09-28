package com.example.adopcion_mascotas.activities

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
import com.example.adopcion_mascotas.ui.theme.Adopcion_mascotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Greeting", "Greeting function called with name: NicolasAndroid")
        Log.i("Greeting", "Greeting function called with name: NicolasAndroid")

        enableEdgeToEdge()
        setContent {
            Adopcion_mascotasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Adopcion_mascotasTheme {
        Greeting("Android")
    }
}