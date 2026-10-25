package com.example.adopcion_mascotas.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data class HistorialPublicaciones(val token: String) : MainRoutes()

    @Serializable
    data class EditarPublicacion(val publicacionId: Long, val token: String) : MainRoutes()

    @Serializable
    data class FotosVacunas(val mascotaId: Long) : MainRoutes()

    @Serializable
    data class CrearPublicacion(val token: String) : MainRoutes()

    @Serializable
    data class DetallePublicacion(val publicacionId: Long, val token: String) : MainRoutes()

    @Serializable
    data class FeedMod(val token: String) : MainRoutes()
    @Serializable
    data class Feed(val token: String) : MainRoutes()

    @Serializable
    data object Recuperacion : MainRoutes()

    @Serializable
    data object Principal : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Registro : MainRoutes()


    @Serializable
    data class Activacion(val correoElectronico: String): MainRoutes()
}