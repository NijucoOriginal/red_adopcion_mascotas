package com.example.adopcion_mascotas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.adopcion_mascotas.features.auth.login.LoginScreen
import com.example.adopcion_mascotas.features.auth.recover.RecoverPasswordScreen
import com.example.adopcion_mascotas.features.auth.register.RegisterScreen
import com.example.adopcion_mascotas.features.createpost.CreatePostScreen
import com.example.adopcion_mascotas.features.feed.FeedScreen
import com.example.adopcion_mascotas.features.home.HomeScreen
import com.example.adopcion_mascotas.features.postdetail.PublicacionDetailScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.FEED) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToRecover = { navController.navigate(Routes.RECOVER_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegistered = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECOVER_PASSWORD) {
            RecoverPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.FEED) {
            FeedScreen(
                onPublicacionClick = { id -> navController.navigate(Routes.publicacionDetail(id)) },
                onCrearPublicacion = { navController.navigate(Routes.CREATE_POST) }
            )
        }

        composable(
            route = Routes.PUBLICACION_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PUBLICACION_ID) { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong(Routes.ARG_PUBLICACION_ID) ?: -1L
            PublicacionDetailScreen(
                publicacionId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CREATE_POST) {
            CreatePostScreen(
                onNavigateBack = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }
    }
}
