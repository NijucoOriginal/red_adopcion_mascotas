package com.example.adopcion_mascotas.core.util

object UsuarioValidators {

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun correoElectronico(value: String): String? = when {
        value.isBlank() -> "Ingresa tu correo electrónico"
        !EMAIL.matches(value.trim()) -> "Correo electrónico inválido"
        else -> null
    }

    fun nombreUsuario(value: String): String? {
        val clean = value.trim().removePrefix("@")
        return when {
            clean.isBlank() -> "Ingresa un nombre de usuario"
            clean.length < 4 -> "Mínimo 4 caracteres"
            clean.any { it.isWhitespace() } -> "No puede contener espacios"
            else -> null
        }
    }

    fun contrasenia(value: String): String? = when {
        value.isBlank() -> "Ingresa tu contraseña"
        value.length < 8 -> "Mínimo 8 caracteres"
        value.none { it.isDigit() } -> "Debe incluir al menos 1 número"
        else -> null
    }

    fun confirmarContrasenia(password: String, confirm: String): String? = when {
        confirm.isBlank() -> "Confirma tu contraseña"
        password != confirm -> "Las contraseñas no coinciden"
        else -> null
    }

    // Opcionales: se validan solo si el usuario decide ingresarlos
    fun telefonoOpcional(value: String): String? =
        if (value.isBlank() || value.count { it.isDigit() } >= 7) null else "Teléfono inválido"

    fun campoOpcional(value: String, label: String, min: Int = 1): String? = when {
        value.isBlank() -> null // si está vacío, no se exige
        value.trim().length < min -> "Mínimo $min caracteres en $label"
        else -> null
    }
}
