package com.huellas.app.core.util

/** Eventos de una sola vez que el ViewModel envía a la pantalla. */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data object Success : UiEvent
}
