package com.example.screentime.models

data class Pelicula(
    val id: Int,
    val nombre: String,
    val genero: String?,
    val fechasalida: String?,
    val sinopsis: String?,
    val emitida: Boolean,
    val foto: String
)
