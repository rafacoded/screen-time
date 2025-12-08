package com.example.screentime.models

data class Recordatorio(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val fecha: String,
    val idPelicula: Int?,
    val idUsuario: Int
)

