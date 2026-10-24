package com.example.adopcion_mascotas.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.adopcion_mascotas.features.activacion.ActivacionScreen
import com.example.adopcion_mascotas.features.login.LoginScreen
import com.example.adopcion_mascotas.features.principal.PrincipalScreen
import com.example.adopcion_mascotas.features.register.RegistroScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Principal // Primera pantalla al abrir la aplicación
        ) {

            composable<MainRoutes.Principal> {
                PrincipalScreen(
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login)
                    },
                    onNavigateToRegistro = {
                        navController.navigate(MainRoutes.Registro)
                    }
                )
            }

            composable<MainRoutes.Login> {
                LoginScreen()
            }

            composable<MainRoutes.Registro> {
                RegistroScreen(
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(MainRoutes.Login) },
                    onNavigateToActivacion = { correo ->
                        navController.navigate(MainRoutes.Activacion(correo))
                    }
                )
            }

            composable<MainRoutes.Activacion> {
                val args = it.toRoute<MainRoutes.Activacion>()
                ActivacionScreen(
                    correoElectronico = args.correoElectronico,
                    onNavigateToBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = { navController.navigate(MainRoutes.Login) }
                )
            }



        }
    }
}