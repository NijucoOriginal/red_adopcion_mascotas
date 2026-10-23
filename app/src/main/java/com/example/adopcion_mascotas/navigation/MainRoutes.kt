package com.example.adopcion_mascotas.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Principal : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Registro : MainRoutes()

}