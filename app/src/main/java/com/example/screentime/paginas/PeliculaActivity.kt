package com.example.screentime.paginas

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import com.example.screentime.database.DBHelper
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.models.Pelicula
import com.example.screentime.session.SessionManager
import com.example.screentime.utils.RecordatorioWorker
import com.example.screentime.utils.esPeliculaEmitida
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit

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
            if (!pelicula.esPeliculaEmitida()) {
                Toast.makeText(this, "Aún no puedes reseñar esta película", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            abrirPopupResena(pelicula.id)
        }

        val btnRecordatorio = findViewById<Button>(R.id.btnRecordatorio)

        if (pelicula.esPeliculaEmitida()) {
            btnRecordatorio.isEnabled = false
            btnRecordatorio.alpha = 0.4f
            btnRecordatorio.text = "Ya estrenada"
        } else {
            btnRecordatorio.isEnabled = true
            btnRecordatorio.alpha = 1f

            btnRecordatorio.setOnClickListener {
                abrirPopupRecordatorio(pelicula)
            }
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
                cargarResenyas(idPelicula)
            } else {
                Toast.makeText(this, "Error al guardar reseña", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun cargarResenyas(idPelicula: Int) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorResenas)
        contenedor.removeAllViews()

        val listaResenyas = dbHelper.getResenyasByPelicula(idPelicula)

        if (listaResenyas.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "Aún no hay reseñas. ¡Sé el primero!"
                textSize = 14f
            }
            contenedor.addView(tvVacio)
            return
        }

        for (res in listaResenyas) {
            val itemView = layoutInflater.inflate(
                R.layout.item_resenya,
                contenedor,
                false
            )

            val ivFoto = itemView.findViewById<ImageView>(R.id.ivFotoUsuario)
            val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreUsuario)
            val rbValoracion = itemView.findViewById<RatingBar>(R.id.rbValoracionItem)
            val tvDescripcion = itemView.findViewById<TextView>(R.id.tvDescripcionResena)

            tvNombre.text = res.nombreUsuario
            rbValoracion.rating = res.resenya.calificacion.toFloat()
            tvDescripcion.text = res.resenya.descripcion

            if (!res.fotoUsuario.isNullOrEmpty()) {
                Glide.with(this)
                    .load(res.fotoUsuario)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .circleCrop()
                    .into(ivFoto)
            }

            contenedor.addView(itemView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun abrirPopupRecordatorio(pelicula: Pelicula) {

        val dialogView = layoutInflater.inflate(R.layout.popup_recordatorio, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreRecordatorio)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionRecordatorio)

        val fechaOriginal = pelicula.fechasalida

        val dialog = AlertDialog.Builder(this)
            .setTitle("Crear recordatorio")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->

                val nombre = etNombre.text.toString()
                val descripcion = etDescripcion.text.toString()

                if (nombre.isEmpty()) {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (fechaOriginal.isNullOrEmpty()) {
                    abrirDatePickerParaRecordatorio(nombre, descripcion, pelicula.id)
                } else {
                    guardarRecordatorio(nombre, descripcion, fechaOriginal, pelicula.id)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun abrirDatePickerParaRecordatorio(
        nombre: String,
        descripcion: String?,
        idPelicula: Int
    ) {
        val datePicker = DatePicker(this)

        AlertDialog.Builder(this)
            .setTitle("Selecciona una fecha")
            .setView(datePicker)
            .setPositiveButton("Guardar") { _, _ ->
                val day = datePicker.dayOfMonth
                val month = datePicker.month + 1
                val year = datePicker.year

                val fecha = "%04d-%02d-%02d".format(year, month, day)

                guardarRecordatorio(nombre, descripcion, fecha, idPelicula)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun guardarRecordatorio(
        nombre: String,
        descripcion: String?,
        fecha: String,
        idPelicula: Int?
    ) {
        val idUsuario = SessionManager(this).getUserId()

        val resultado = dbHelper.insertRecordatorio(
            nombre = nombre,
            descripcion = descripcion,
            fecha = fecha,
            id_pelicula = idPelicula,
            id_usuario = idUsuario
        )

        if (resultado > 0) {
            Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show()

            programarRecordatorioWorker(
                idRecordatorio = resultado.toInt(),
                nombre = nombre,
                descripcion = descripcion,
                fecha = fecha
            )
        } else {
            Toast.makeText(this, "Error al guardar recordatorio", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun programarRecordatorioWorker(
        idRecordatorio: Int,
        nombre: String,
        descripcion: String?,
        fecha: String
    ) {
        val localDate = LocalDate.parse(fecha)
        val fechaNotificar = localDate.minusDays(1) // 1 día antes

        val fechaNotiMillis = fechaNotificar
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val ahoraMillis = System.currentTimeMillis()
        val delay = fechaNotiMillis - ahoraMillis

//        val delay = 5 * 60 * 1000L  // Prueba para notificaciones en 5 minutos


        if (delay <= 0) return

        val data = workDataOf(
            "id" to idRecordatorio,
            "titulo" to nombre,
            "descripcion" to (descripcion ?: "")
        )

        val request = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(request)

        Toast.makeText(this, "🔔 Notificación programada en 5 minutos", Toast.LENGTH_SHORT).show()

    }



}
