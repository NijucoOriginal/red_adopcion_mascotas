package com.example.adopcion_mascotas.navigation

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RECOVER_PASSWORD = "recover_password"
    const val FEED = "feed"
    const val CREATE_POST = "create_post"

    const val ARG_PUBLICACION_ID = "publicacionId"
    const val PUBLICACION_DETAIL = "publicacion_detail/{$ARG_PUBLICACION_ID}"

    fun publicacionDetail(id: Long) = "publicacion_detail/$id"
}