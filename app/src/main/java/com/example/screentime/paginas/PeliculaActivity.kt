package com.example.screentime.paginas

import Conection.DBHelper
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.screentime.R
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PeliculaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

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

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val chipPendiente = findViewById<Chip>(R.id.chipPendiente)
        val chipViendo = findViewById<Chip>(R.id.chipViendo)
        val chipVista = findViewById<Chip>(R.id.chipVista)

        val year = if (!pelicula.fechasalida.isNullOrEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(pelicula.fechasalida)
            SimpleDateFormat("yyyy", Locale.getDefault()).format(date!!)
        } else "¿?"

        tvInfo.text = "$year · ${pelicula.genero ?: "Sin género"}"

        when (pelicula.emitida) {
            false -> chipPendiente.isChecked = true
            true -> chipVista.isChecked = true
        }

    }
}
