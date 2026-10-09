package com.example.adopcion_mascotas.domain.repository

import com.example.adopcion_mascotas.domain.model.*

object FakePublicacionRepository {
    private val _publicaciones = mutableListOf<Publicacion>()

    val publicaciones: List<Publicacion> get() = _publicaciones

    fun addPublicacion(pub: Publicacion) {
        _publicaciones.add(pub)
    }

    init {
        _publicaciones.addAll(
            listOf(
                Publicacion(
                    id = 1,
                    cantidad_interesa = 3,
                    comentarios_activados = true,
                    es_de_moderador = false,
                    fecha_publicacion = java.time.LocalDateTime.now(),
                    descripcion = "Perro mestizo en adopción, muy juguetón.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ADOPCION,
                    usuario_duenio = 101,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                ),
                Publicacion(
                    id = 2,
                    cantidad_interesa = 0,
                    comentarios_activados = true,
                    es_de_moderador = false,
                    fecha_publicacion = java.time.LocalDateTime.now().minusDays(1),
                    descripcion = "Gato reportado como perdido en el barrio Granada.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ENCONTRADOS,
                    usuario_duenio = 102,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                ),
                Publicacion(
                    id = 3,
                    cantidad_interesa = 1,
                    comentarios_activados = false,
                    es_de_moderador = true,
                    fecha_publicacion = java.time.LocalDateTime.now().minusDays(2),
                    descripcion = "Caso cerrado: mascota encontrada y reunida con su dueño.",
                    estado_publicacion = EstadoPublicacion.ADMITIDO,
                    categoria = CategoriaPublicacion.ENCONTRADOS,
                    usuario_duenio = 103,
                    latitud = 0.0,
                    longitud = 0.0,
                    nombre_ubicacion = "Armenia, Quindío"
                )
            )
        )
    }
}
