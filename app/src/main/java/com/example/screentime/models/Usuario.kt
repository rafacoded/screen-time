package com.example.screentime.models

data class Usuario (
    val id : Integer,
    val nombre: String,
    val descripcion: String?,
    val contrasenya: String,
    val email : String,
    val fotoperfil : String?,
    val id_resenya : Integer?
)

