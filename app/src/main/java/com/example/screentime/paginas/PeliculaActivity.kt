package com.example.screentime.paginas

import android.os.Build
import com.example.screentime.database.DBHelper
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.utils.esPeliculaEmitida
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class PeliculaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelicula)

        dbHelper = DBHelper(this)

        // 1. Recibiendo ID...

        val idPelicula = intent.getIntExtra("peliculaId", -1)

        if (idPelicula == -1) {
            finish()
            return
        }

        // 2. Recuperar película

        val pelicula = dbHelper.getPeliculaById(idPelicula)

        if (pelicula == null) {
            finish()
            return
        }

        // 3. Asociar atributos a elementos del activity_pelicula

        val ivPortada = findViewById<ImageView>(R.id.ivPortada)
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvInfo = findViewById<TextView>(R.id.tvFecha)
        val chipEstado = findViewById<Chip>(R.id.chipEstadoDetalle)

        Glide.with(this)
            .load(pelicula.foto)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(ivPortada)


        tvNombre.text = pelicula.nombre

        tvDescripcion.text = pelicula.sinopsis

        val year = if (!pelicula.fechasalida.isNullOrEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(pelicula.fechasalida)
            SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)
        } else "-"

        tvInfo.text = "$year · ${pelicula.genero ?: "Sin género"}"

        if (pelicula.esPeliculaEmitida()) {
            chipEstado.text = "Vista"
        } else {
            chipEstado.text = "Pendiente"
        }



    }
}
