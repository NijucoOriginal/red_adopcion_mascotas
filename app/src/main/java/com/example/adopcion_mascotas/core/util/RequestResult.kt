package com.example.adopcion_mascotas.core.util

sealed class RequestResult {
    object Loading : RequestResult()                            // La operación está en curso
    data class Success(val message: String) : RequestResult()   // La operación terminó bien
    data class Failure(val errorMessage: String) : RequestResult() // La operación falló
}