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
import com.example.adopcion_mascotas.features.createpost.CrearPublicacionScreen
import com.example.adopcion_mascotas.features.feed.FeedModScreen
import com.example.adopcion_mascotas.features.feed.FeedScreen
import com.example.adopcion_mascotas.features.feed.HistorialPublicacionesScreen
import com.example.adopcion_mascotas.features.historial.EditarPublicacionScreen
import com.example.adopcion_mascotas.features.login.LoginScreen
import com.example.adopcion_mascotas.features.principal.PrincipalScreen
import com.example.adopcion_mascotas.features.publicacion.FotosVacunasScreen
import com.example.adopcion_mascotas.features.publicacion.PublicacionScreen
import com.example.adopcion_mascotas.features.recuperacion.RecuperacionScreen
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
                LoginScreen(
                    onNavigateToRegistro = { navController.navigate(MainRoutes.Registro) },
                    onNavigateToRecuperacion = { navController.navigate(MainRoutes.Recuperacion) },
                    onNavigateToFeed = { token ->
                        navController.navigate(MainRoutes.Feed(token)) {
                            popUpTo(MainRoutes.Login) { inclusive = true }
                        }
                    },
                    onNavigateToFeedMod = { token ->
                        navController.navigate(MainRoutes.FeedMod(token)) {
                            popUpTo(MainRoutes.Login) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.Feed> {
                val args = it.toRoute<MainRoutes.Feed>()
                FeedScreen(
                    token = args.token,
                    onSesionExpirada = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onPublicacionClick = { id ->
                        navController.navigate(MainRoutes.DetallePublicacion(id, args.token))
                    },
                    onCrearPublicacion = {
                        navController.navigate(MainRoutes.CrearPublicacion(args.token))
                    },
                    onVerPerfilClick = { /* pendiente: perfil */ },
                    onVerHistorialClick = {
                        navController.navigate(MainRoutes.HistorialPublicaciones(args.token))
                    },
                    onCerrarSesion = {
                        navController.navigate(MainRoutes.Principal) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.HistorialPublicaciones> {
                val args = it.toRoute<MainRoutes.HistorialPublicaciones>()
                HistorialPublicacionesScreen(
                    token = args.token,
                    onNavigateBack = { navController.popBackStack() },
                    onSesionExpirada = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onModificarClick = { publicacionId ->
                        navController.navigate(MainRoutes.EditarPublicacion(publicacionId, args.token))
                    }
                )
            }

            composable<MainRoutes.EditarPublicacion> {
                val args = it.toRoute<MainRoutes.EditarPublicacion>()
                EditarPublicacionScreen(
                    publicacionId = args.publicacionId,
                    token = args.token,
                    onNavigateBack = { navController.popBackStack() },
                    onSesionExpirada = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.DetallePublicacion> {
                val args = it.toRoute<MainRoutes.DetallePublicacion>()
                PublicacionScreen(
                    publicacionId = args.publicacionId,
                    token = args.token,
                    onNavigateBack = { navController.popBackStack() },
                    onSesionExpirada = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onVerFotosClick = { mascotaId ->
                        navController.navigate(MainRoutes.FotosVacunas(mascotaId))
                    }
                )
            }

            composable<MainRoutes.FotosVacunas> {
                val args = it.toRoute<MainRoutes.FotosVacunas>()
                FotosVacunasScreen(
                    mascotaId = args.mascotaId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<MainRoutes.FeedMod> {
                val args = it.toRoute<MainRoutes.FeedMod>()
                FeedModScreen(token = args.token)
            }

            composable<MainRoutes.Recuperacion> {
                RecuperacionScreen()
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

            composable<MainRoutes.CrearPublicacion> {
                val args = it.toRoute<MainRoutes.CrearPublicacion>()
                CrearPublicacionScreen(
                    token = args.token,
                    onNavigateBack = { navController.popBackStack() },
                    onPublicacionCreada = { navController.popBackStack() }
                )
            }

        }
    }
}