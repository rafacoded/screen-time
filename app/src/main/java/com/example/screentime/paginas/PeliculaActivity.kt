package com.example.screentime.paginas

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import com.example.screentime.database.DBHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.session.SessionManager
import com.example.screentime.utils.esPeliculaEmitida
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class PeliculaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelicula)

        dbHelper = DBHelper(this)

        val idPelicula = intent.getIntExtra("peliculaId", -1)

        if (idPelicula == -1) {
            finish()
            return
        }

        val pelicula = dbHelper.getPeliculaById(idPelicula)

        if (pelicula == null) {
            finish()
            return
        }

        val btnReview = findViewById<Button>(R.id.btnReview)

        btnReview.setOnClickListener {
            abrirPopupResena(idPelicula)
        }

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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun abrirPopupResena(idPelicula: Int) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_resenya, null)

        val ratingBar = dialogView.findViewById<RatingBar>(R.id.rbValoracion)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarResena)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        btnGuardar.setOnClickListener {

            val valoracion = ratingBar.rating.toInt()
            val descripcion = etDescripcion.text.toString()

            if (descripcion.isEmpty()) {
                etDescripcion.error = "La reseña no puede estar vacía"
                return@setOnClickListener
            }

            val sessionManager = SessionManager(this)
            val idUsuario = sessionManager.getUserId()

            val fecha = LocalDate.now().toString()

            val resultado = dbHelper.insertResenya(
                descripcion = descripcion,
                calificacion = valoracion,
                fecha = fecha,
                id_pelicula = idPelicula,
                id_usuario = idUsuario
            )

            if (resultado > 0) {
                Toast.makeText(this, "Reseña guardada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error al guardar reseña", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
