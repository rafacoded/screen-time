package com.example.screentime.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.screentime.models.Pelicula
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun Pelicula.esPeliculaEmitida(): Boolean {
    val fecha = fechasalida ?: return false
    val fechaPelicula = LocalDate.parse(fecha)
    return !fechaPelicula.isAfter(LocalDate.now())
}
