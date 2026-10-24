package com.example.adopcion_mascotas.core.util

import com.example.adopcion_mascotas.domain.model.EstadoUsuario
import com.example.adopcion_mascotas.domain.model.Rol
import com.example.adopcion_mascotas.domain.model.Usuario


val usuarios = mutableListOf<Usuario>()
val codigosGenerados = mutableListOf<String?>()

fun registrarUsuario(
    nombre_usuario: String,
    correo_electronico: String,
    contrasenia: String,
    primer_nombre: String,
    segundo_nombre: String,
    primer_apellido: String,
    segundo_apellido: String,
    direccion: String,
    telefono: String,
    codigoMultiproposito: String
): Boolean {
    val id: Long=(0..100).random().toLong()
    var usuarioNuevo=buscarUsuarioExistente(correo_electronico)
    if(usuarioNuevo==null)
    {
        usuarioNuevo= Usuario(id,
            nombre_usuario,
            correo_electronico,
            contrasenia,
            Rol.USUARIO,
            codigoMultiproposito,
            primer_nombre,
            segundo_nombre,
            primer_apellido,
            segundo_apellido,
            direccion,
            telefono,
            EstadoUsuario.PENDIENTE)
        usuarios.add(usuarioNuevo)
    }
    else
    {
        return false
    }
    return true

}

fun buscarUsuarioExistente(correo_electronico: String): Usuario? {
    usuarios.forEach { usuario ->
        if(correo_electronico==usuario.correo_electronico)
        {
            return usuario;
        }
    }
    return null;
}

fun verificarCodigo(codigo: String,correo_electronico: String): Boolean {
    val usuario=buscarUsuarioExistente(correo_electronico)
    if(codigo == usuario?.codigo_multiproposito)
    {
        return true
    }
    return false
}

fun llenarCodigosGenerados() {
    usuarios.forEach { usuario  ->
        codigosGenerados.add(usuario.codigo_multiproposito)
    }
}


fun generarCodigoUnico(): String {
    var codigo: String

    do {
        codigo = (10000..99999).random().toString()
    } while (codigo in codigosGenerados)

    codigosGenerados.add(codigo)

    return codigo
}